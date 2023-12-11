package com.kuzmich.exceptions;

import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
public class ErrorResponse {
    String domain;
    HttpStatus status;
    String errorMessage;
}
