package com.loan_microservice.infraestructure.entryPoints.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record LoanResponseDto(
        Long id,
        String customerDni,
        BigDecimal amount,
        Integer termInMonths,
        String loanType,
        String loanStatus,
        LocalDateTime createdAt
) {
}
