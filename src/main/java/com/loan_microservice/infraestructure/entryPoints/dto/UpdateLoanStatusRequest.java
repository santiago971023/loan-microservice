package com.loan_microservice.infraestructure.entryPoints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateLoanStatusRequest {


    @NotBlank(message = "El nuevo estado del la solicitud es obligatorio.")
    @Pattern(
            regexp = "APPROVED|REJECTED",
            message = "El nuevo estado de la solicitud debe ser 'APPROVED' o 'REJECTED'."
    )
    private String newStatus;

}
