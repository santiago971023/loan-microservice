package com.loan_microservice.infraestructure.adapters.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("loans")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanEntity {

    @Id
    private Long id;

    private String clientDni;
    private BigDecimal amount;
    private Integer termInMonths;

    private String loanType;
    private String loanStatus;

    private LocalDateTime createdAt;

}
