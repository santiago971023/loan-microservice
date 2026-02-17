package com.loan_microservice.application.exception;

public class MinLoanAmountException extends RuntimeException {
    public MinLoanAmountException(String message) {
        super(message);
    }
}
