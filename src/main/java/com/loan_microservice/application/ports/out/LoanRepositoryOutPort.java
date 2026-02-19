package com.loan_microservice.application.ports.out;

import com.loan_microservice.domain.model.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanRepositoryOutPort {

    Mono<LoanApplication> save(LoanApplication loanApplication);

}
