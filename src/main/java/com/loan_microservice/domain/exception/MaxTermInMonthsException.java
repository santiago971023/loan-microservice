package com.loan_microservice.domain.exception;

public class MaxTermInMonthsException extends RuntimeException {
    public MaxTermInMonthsException(String message) {
        super(message);
    }
}
