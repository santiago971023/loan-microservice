package com.loan_microservice.application.ports.out;

import com.loan_microservice.domain.model.loan.LoanApplication;
import com.loan_microservice.domain.model.loan.LoanStatus;
import com.loan_microservice.domain.model.pageable.DomainPageable;
import com.loan_microservice.domain.model.pageable.PaginationResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanRepositoryOutPort {

    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<PaginationResponse<LoanApplication>> findLoanByStatus(List<LoanStatus> status, DomainPageable pageable);
    Mono<LoanApplication> findLoanApplicationById(Long id);
}
