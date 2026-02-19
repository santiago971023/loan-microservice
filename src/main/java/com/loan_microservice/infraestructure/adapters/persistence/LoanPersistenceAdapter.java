package com.loan_microservice.infraestructure.adapters.persistence;

import com.loan_microservice.application.ports.out.LoanRepositoryOutPort;
import com.loan_microservice.domain.model.LoanApplication;
import com.loan_microservice.infraestructure.adapters.persistence.mapper.LoanPersistenceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
}
