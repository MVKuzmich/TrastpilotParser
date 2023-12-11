package com.kuzmich.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(DomainNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleDomainNotFoundErrorException(DomainNotFoundException ex) {
        return ResponseEntity.status(ex.getErrorResponse().getStatus())
                .body(ex.getErrorResponse());
    }
}
