package com.kuzmich.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

@Value
public class ResponseDto {

    Integer reviewsCount;
    Float rating;
}
