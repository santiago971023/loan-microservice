package com.loan_microservice.domain.model.loan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplication {

    private Long id;
    private String customerDni;
    private BigDecimal amount;
    private Integer termInMonths;
    private LoanType loanType;
    private LoanStatus loanStatus;
    private LocalDateTime createdAt;

    public LoanApplication() {
    }

    public LoanApplication(String customerDni, BigDecimal amount, Integer termInMonths, LoanType loanType, LoanStatus loanStatus, LocalDateTime createdAt) {
        this.customerDni = customerDni;
        this.amount = amount;
        this.termInMonths = termInMonths;
        this.loanType = loanType;
        this.loanStatus = loanStatus;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerDni() {
        return customerDni;
    }

    public void setCustomerDni(String customerDni) {
        this.customerDni = customerDni;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTermInMonths() {
        return termInMonths;
    }

    public void setTermInMonths(Integer termInMonths) {
        this.termInMonths = termInMonths;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
