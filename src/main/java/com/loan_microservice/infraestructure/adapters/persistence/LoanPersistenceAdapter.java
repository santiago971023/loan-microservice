package com.loan_microservice.infraestructure.adapters.persistence;

import com.loan_microservice.application.ports.out.LoanRepositoryOutPort;
import com.loan_microservice.domain.model.loan.LoanApplication;
import com.loan_microservice.domain.model.loan.LoanStatus;
import com.loan_microservice.domain.model.pageable.DomainPageable;
import com.loan_microservice.domain.model.pageable.PaginationResponse;
import com.loan_microservice.infraestructure.adapters.persistence.mapper.LoanPersistenceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;



import java.util.List;

@Component
@Slf4j
public class LoanPersistenceAdapter implements LoanRepositoryOutPort {

    private final LoanRepository loanRepository;
    private final LoanPersistenceMapper mapper;

    public LoanPersistenceAdapter(LoanRepository loanRepository, LoanPersistenceMapper mapper) {
        this.loanRepository = loanRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        LoanEntity entity = mapper.toEntity(loanApplication);
        log.debug("Guardando entidad de préstamo en base de datos");
        return loanRepository.save(entity)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<PaginationResponse<LoanApplication>> findLoanByStatus(List<LoanStatus> status, DomainPageable pageable) {

        List<String> statusString = status.stream().map(Enum::name).toList();
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        return Mono.zip(
                loanRepository.findByLoanStatusIn(statusString, pageRequest).collectList(),
                loanRepository.countByLoanStatusIn(statusString),
                (listOfLoansEntities, numberOfLoans) -> {
                    List<LoanApplication> listOfDomainLoans = listOfLoansEntities.stream().map(mapper::toDomain).toList();
                    return new PaginationResponse<LoanApplication>(listOfDomainLoans, numberOfLoans, pageable);
                }
        );

    }

    @Override
    public Mono<LoanApplication> findLoanApplicationById(Long id) {
        return loanRepository.findById(id)
                .map(mapper::toDomain);
    }

}
