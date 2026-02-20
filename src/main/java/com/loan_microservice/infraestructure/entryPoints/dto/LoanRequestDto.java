package com.loan_microservice.infraestructure.entryPoints.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public record LoanRequestDto(

        @NotBlank(message = "El documento de identidad del cliente es obligatorio")
        @Pattern(regexp = "^\\d+$", message = "El campo 'dni' solo debe contener números.")
        String customerDni,

        @NotNull(message = "El monto del préstamo es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        BigDecimal amount,

        @NotNull(message = "El plazo en meses es obligatorio")
        @Min(value = 1, message = "El plazo debe ser al menos 1 mes")
        Integer termInMonths,

        @NotBlank(message = "El tipo de préstamo es obligatorio")
        @Pattern(
                regexp = "PERSONAL|VEHICLE|MORTGAGE|EDUCATION",
                message = "El tipo de préstamo debe ser: PERSONAL, VEHICLE, MORTGAGE o EDUCATION"
        )
        String loanType
) {
}
