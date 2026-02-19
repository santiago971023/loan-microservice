package com.loan_microservice.infraestructure.entryPoints.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record LoanRequestDto(
        @NotBlank(message = "El documento de identidad del cliente es obligatorio")
        String customerDni,

        @NotNull(message = "El monto del préstamo es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        BigDecimal amount,

        @NotNull(message = "El plazo en meses es obligatorio")
        @Min(value = 1, message = "El plazo debe ser al menos 1 mes")
        Integer termInMonths,

        @NotBlank(message = "El tipo de préstamo es obligatorio")
        String loanType
) {
}
