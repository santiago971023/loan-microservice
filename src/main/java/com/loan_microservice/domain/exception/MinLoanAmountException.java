package com.loan_microservice.domain.exception;

public class MinLoanAmountException extends RuntimeException {
    public MinLoanAmountException(String message) {
        super(message);
    }
}
