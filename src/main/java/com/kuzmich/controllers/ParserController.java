package com.kuzmich.controllers;

import com.kuzmich.dto.ResponseDto;
import com.kuzmich.service.ParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ParserController {

    private final ParserService parserService;

    @GetMapping("/reviews/{domain}")
    public Mono<ResponseDto> getParsingResponse(@PathVariable String domain) {
        return parserService.parse(domain);
    }
}
