package com.loan_microservice.infraestructure.adapters.persistence.mapper;

import com.loan_microservice.domain.model.loan.LoanApplication;
import com.loan_microservice.infraestructure.adapters.persistence.LoanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanPersistenceMapper {

    LoanEntity toEntity(LoanApplication domain);
    LoanApplication toDomain(LoanEntity entity);
}
