package com.loan_microservice.domain.exception;

public class MinTermInMonthsException extends RuntimeException {
    public MinTermInMonthsException(String message) {
        super(message);
    }
}
