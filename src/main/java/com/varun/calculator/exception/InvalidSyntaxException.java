package com.varun.calculator.exception;

/**
 * @author Varun Srivastava
 */
public class InvalidSyntaxException extends RuntimeException {

    public InvalidSyntaxException(String message) {
        super(message);
    }
}