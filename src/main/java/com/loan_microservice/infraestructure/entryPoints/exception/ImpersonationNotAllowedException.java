package com.loan_microservice.infraestructure.entryPoints.exception;

public class ImpersonationNotAllowedException extends RuntimeException {
    public ImpersonationNotAllowedException(String message) {
        super(message);
    }
}
