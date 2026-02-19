package com.loan_microservice.infraestructure.entryPoints;

import com.loan_microservice.application.ports.in.CreateLoanInputPort;
import com.loan_microservice.infraestructure.entryPoints.dto.LoanRequestDto;
import com.loan_microservice.infraestructure.mapper.LoanRestMapper;
import com.loan_microservice.infraestructure.shared.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
public class LoanHandler {

    private final CreateLoanInputPort createLoanInputPort;
    private final LoanRestMapper loanRestMapper;
    private final RequestValidator requestValidator;

    public LoanHandler(CreateLoanInputPort createLoanInputPort, LoanRestMapper loanRestMapper, RequestValidator requestValidator) {
        this.createLoanInputPort = createLoanInputPort;
        this.loanRestMapper = loanRestMapper;
        this.requestValidator = requestValidator;
    }

    public Mono<ServerResponse> createLoan(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanRequestDto.class)
                .doOnNext(dto -> log.info("Solicitud de préstamo recibida para DNI: {}", dto.customerDni()))
                .flatMap(requestValidator::validate)
                .map(loanRestMapper::toDomain)
                .flatMap(createLoanInputPort::create)
                .map(loanRestMapper::toResponse)
                .doOnNext(response -> log.info("Préstamo creado exitosamente con ID: {}", response.id()))
                .flatMap(loanResponse -> ServerResponse
                        .created(URI.create("/api/v1/loans/" + loanResponse.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loanResponse)
                );
    }
}
