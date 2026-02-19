package com.loan_microservice.application.ports.in;

import com.loan_microservice.domain.model.LoanApplication;
import reactor.core.publisher.Mono;

public interface CreateLoanInputPort {

    Mono<LoanApplication> create(LoanApplication loanApplication);

}
