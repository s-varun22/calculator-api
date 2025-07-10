package com.varun.calculator.exception;

/**
 * @author Varun Srivastava
 */
public class InvalidExpressionException extends RuntimeException {

    public InvalidExpressionException(String message) {
        super(message);
    }
}