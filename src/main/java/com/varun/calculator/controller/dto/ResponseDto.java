package com.varun.calculator.controller.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResponseDto {
    Double result;

    public ResponseDto(Double result) {
        this.result = result;
    }
}