package com.loan_microservice.application.exception;

public class MaxTermInMonthsException extends RuntimeException {
    public MaxTermInMonthsException(String message) {
        super(message);
    }
}
