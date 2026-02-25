package com.loan_microservice.infraestructure.security;

public record CustomPrincipal(Long userId, String email) {
}
