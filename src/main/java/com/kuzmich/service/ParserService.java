package com.kuzmich.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kuzmich.dto.ParsingResult;
import com.kuzmich.exceptions.DomainNotFoundException;
import com.kuzmich.exceptions.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.util.MimeTypeUtils.TEXT_HTML;

@Service
@Slf4j
public class ParserService {

    private static final String BASE_URL = "https://www.trustpilot.com/review/";
    private static final String RATING_ATTRIBUTE = "data-rating-typography";
    private static final String REVIEWS_COUNT_CLASS = "typography_body-l__KUYFJ typography_appearance-subtle__8_H2l styles_text__W4hWi";
    private final WebClient client;
    private final Cache<String, ParsingResult> parsingDataCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofDays(1L))
            .maximumSize(10000L)
            .build();

    public ParserService(WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder.baseUrl(BASE_URL)
                .exchangeStrategies(ExchangeStrategies.builder().codecs(this::acceptedCodecs).build())
                .build();
    }

    public Mono<ParsingResult> parse(String domain) {
        ParsingResult parsingData = parsingDataCache.getIfPresent(domain);
        return Mono.justOrEmpty(parsingData)
                .switchIfEmpty(
                        client
                                .get()
                                .uri(domain)
                                .exchangeToMono(clientResponse -> {
                                    log.info("Check response...");
                                    if (clientResponse.statusCode().is2xxSuccessful()) {
                                        return clientResponse.bodyToMono(String.class)
                                                .map(this::parseHtml)
                                                .doOnNext(result -> {
                                                    parsingDataCache.put(domain, result);
                                                    log.info("Info {} about Domain {} put in cache", result.toString(), domain);
                                                });
                                    } else {
                                        log.info("Domain {} is not found", domain);
                                        throw new DomainNotFoundException(new ErrorResponse(domain, HttpStatus.NOT_FOUND, "Domain is not found"));
                                    }
                                })
                );

    }

    private ParsingResult parseHtml(String html) {
        Document document = Jsoup.parse(html);
        String rating = document.getElementsByAttribute(RATING_ATTRIBUTE).get(0).text();
        String reviews = document.getElementsByClass(REVIEWS_COUNT_CLASS).get(0).text().replaceAll("[^0-9]", "");

        return new ParsingResult(Integer.parseInt(reviews), Float.parseFloat(rating));
    }

    private void acceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
        clientCodecConfigurer.customCodecs().encoder(new Jackson2JsonEncoder(new ObjectMapper(), TEXT_HTML));
        clientCodecConfigurer.customCodecs().decoder(new Jackson2JsonDecoder(new ObjectMapper(), TEXT_HTML));
    }
}
