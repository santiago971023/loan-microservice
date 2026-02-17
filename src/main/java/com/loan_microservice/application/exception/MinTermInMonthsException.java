package com.loan_microservice.application.exception;

public class MinTermInMonthsException extends RuntimeException {
    public MinTermInMonthsException(String message) {
        super(message);
    }
}
