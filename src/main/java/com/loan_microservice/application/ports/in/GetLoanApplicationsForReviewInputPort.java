package com.loan_microservice.application.ports.in;

import com.loan_microservice.domain.model.loan.LoanApplicationDetail;
import com.loan_microservice.domain.model.loan.LoanStatus;
import com.loan_microservice.domain.model.pageable.DomainPageable;
import com.loan_microservice.domain.model.pageable.PaginationResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GetLoanApplicationsForReviewInputPort {

    Mono<PaginationResponse<LoanApplicationDetail>> getLoanApps(List<LoanStatus> statuses, DomainPageable domainPageable);

}
