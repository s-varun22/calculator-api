package com.varun.calculator.exception;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDto {

    String failureReason;
}
