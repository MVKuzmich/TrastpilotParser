package com.kuzmich.exceptions;

import lombok.Getter;

@Getter
public class DomainNotFoundException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public DomainNotFoundException(ErrorResponse errorResponse) {
        super(errorResponse.getErrorMessage());
        this.errorResponse = errorResponse;
    }
}
