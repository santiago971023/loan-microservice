package com.loan_microservice.domain.model.loan;

import com.loan_microservice.domain.model.user.User;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class LoanApplicationDetail {

    private LoanApplication loanApplication;
    private User user;

    private BigDecimal monthlyPayment;


}
