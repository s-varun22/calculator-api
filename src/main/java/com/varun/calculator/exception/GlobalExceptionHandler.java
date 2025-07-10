package com.varun.calculator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidExpressionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDto> handleException(InvalidExpressionException ex) {
        return new ResponseEntity<>(ErrorDto.builder()
                .failureReason(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidSyntaxException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDto> handleException(InvalidSyntaxException ex) {
        return new ResponseEntity<>(ErrorDto.builder()
                .failureReason(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDto> handleGenericException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(ErrorDto.builder()
                .failureReason("Error Occurred while processing the request")
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
