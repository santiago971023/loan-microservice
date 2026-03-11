package com.loan_microservice.domain.model.loan;

import com.loan_microservice.domain.model.user.User;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class LoanApplicationDetail {

    private LoanApplication loanApplication;
    private User user;

    private BigDecimal monthlyPayment;

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
}
