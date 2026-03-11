package com.loan_microservice.infraestructure.adapters.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Pageable;



import java.util.List;

@Repository
public interface LoanRepository extends ReactiveCrudRepository<LoanEntity, Long> {
    Flux<LoanEntity> findByLoanStatusIn(List<String> statuses, Pageable pageable);
    Mono<Long> countByLoanStatusIn(List<String> statuses);
}
