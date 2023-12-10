package com.kuzmich.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzmich.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.util.MimeTypeUtils.TEXT_HTML;

@Service
public class ParserService {

    private static final String BASE_URL = "https://www.trustpilot.com/review/";
    private static final String RATING_ATTRIBUTE = "data-rating-typography";
    private static final String REVIEWS_COUNT_CLASS = "typography_body-l__KUYFJ typography_appearance-subtle__8_H2l styles_text__W4hWi";
    private final WebClient client;

    public ParserService(WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder.baseUrl(BASE_URL)
                .exchangeStrategies(ExchangeStrategies.builder().codecs(this::acceptedCodecs).build())
                .build();
    }

    public Mono<ResponseDto> parse(String domain) {
        return client
                .get()
                .uri(domain)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseHtml);
    }

    private ResponseDto parseHtml(String html) {
        Document document = Jsoup.parse(html);
        String rating = document.getElementsByAttribute(RATING_ATTRIBUTE).get(0).text();
        String reviews = document.getElementsByClass(REVIEWS_COUNT_CLASS).get(0).text().replaceAll("[^0-9]", "");

        return new ResponseDto(Integer.parseInt(reviews), Float.parseFloat(rating));
    }

    private void acceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
        clientCodecConfigurer.customCodecs().encoder(new Jackson2JsonEncoder(new ObjectMapper(), TEXT_HTML));
        clientCodecConfigurer.customCodecs().decoder(new Jackson2JsonDecoder(new ObjectMapper(), TEXT_HTML));
    }
}
