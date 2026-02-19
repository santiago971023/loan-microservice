package com.loan_microservice.infraestructure.mapper;

import com.loan_microservice.domain.model.LoanApplication;
import com.loan_microservice.infraestructure.entryPoints.dto.LoanRequestDto;
import com.loan_microservice.infraestructure.entryPoints.dto.LoanResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanRestMapper {

    LoanResponseDto toResponse(LoanApplication loanApplication);
    LoanApplication toDomain(LoanRequestDto loanRequest);

}
