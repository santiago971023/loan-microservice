package com.loan_microservice.domain.exception;

public class InvalidLoanTypeException extends RuntimeException {

    public InvalidLoanTypeException(String message) {
        super(message);
    }
}
