package com.loan_microservice.infraestructure.entryPoints;

import com.loan_microservice.application.ports.in.ApproveOrRejectLoanInputPort;
import com.loan_microservice.application.ports.in.CreateLoanInputPort;
import com.loan_microservice.application.ports.in.GetLoanApplicationsForReviewInputPort;
import com.loan_microservice.domain.model.loan.LoanApplication;
import com.loan_microservice.domain.model.loan.LoanApplicationDetail;
import com.loan_microservice.domain.model.loan.LoanStatus;
import com.loan_microservice.domain.model.pageable.DomainPageable;
import com.loan_microservice.domain.model.pageable.PaginationResponse;
import com.loan_microservice.infraestructure.entryPoints.dto.LoanRequestDto;
import com.loan_microservice.infraestructure.entryPoints.dto.UpdateLoanStatusRequest;
import com.loan_microservice.infraestructure.entryPoints.exception.ImpersonationNotAllowedException;
import com.loan_microservice.infraestructure.mapper.LoanRestMapper;
import com.loan_microservice.infraestructure.security.CustomPrincipal;
import com.loan_microservice.infraestructure.shared.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.LineNumberInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoanHandler {

    private final CreateLoanInputPort createLoanInputPort;
    private final GetLoanApplicationsForReviewInputPort getLoanApplicationsForReviewInputPort;
    private final LoanRestMapper loanRestMapper;
    private final RequestValidator requestValidator;
    private final ApproveOrRejectLoanInputPort approveOrRejectLoanInputPort;

    public LoanHandler(CreateLoanInputPort createLoanInputPort, GetLoanApplicationsForReviewInputPort getLoanApplicationsForReviewInputPort, LoanRestMapper loanRestMapper, RequestValidator requestValidator, ApproveOrRejectLoanInputPort approveOrRejectLoanInputPort) {
        this.createLoanInputPort = createLoanInputPort;
        this.getLoanApplicationsForReviewInputPort = getLoanApplicationsForReviewInputPort;
        this.loanRestMapper = loanRestMapper;
        this.requestValidator = requestValidator;
        this.approveOrRejectLoanInputPort = approveOrRejectLoanInputPort;
    }

    public Mono<ServerResponse> createLoan(ServerRequest serverRequest) {

        // FLujo del body
        Mono<LoanRequestDto> dtoMono = serverRequest.bodyToMono(LoanRequestDto.class)
            .doOnNext(dto -> log.info("Solicitud para el DNI: {}", dto.customerDni()));

        // Flujo de security
        Mono<CustomPrincipal> principalMono = ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (CustomPrincipal) ctx.getAuthentication().getPrincipal());


        return Mono.zip(dtoMono, principalMono)
                .flatMap(tuple -> {
                    LoanRequestDto dto = tuple.getT1();
                    CustomPrincipal principal = tuple.getT2();

                    // Validación de seguridad, comparamos el DNI
                    if(!principal.dni().equals(dto.customerDni())){
                        log.error("SUPLANTACIÓN: Token DNI {} intenta pedir un crédito para DNI {}", principal.dni(), dto.customerDni());
                        return Mono.error(new ImpersonationNotAllowedException("No puedes solicitar un crédito para otra persona"));
                    }
                    return Mono.just(dto);
                })

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


    public Mono<ServerResponse> listLoanApps(ServerRequest serverRequest){
        int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(10);

        DomainPageable domainPageable = DomainPageable.builder()
                .pageNumber(page)
                .pageSize(size)
                .build();

        List<String> statusesString = serverRequest.queryParam("statuses")
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(List.of("PENDING_REVIEW"));

        List<LoanStatus> statusesEnum = statusesString.stream()
                .map(String::toUpperCase)
                .map(LoanStatus::valueOf)
                .toList();

        Mono<PaginationResponse<LoanApplicationDetail>> result = getLoanApplicationsForReviewInputPort.getLoanApps(statusesEnum, domainPageable);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result, PaginationResponse.class);
    }

    public Mono<ServerResponse> updateLoanStatus(ServerRequest serverRequest) {

        // 1. Extraemos el ID de forma segura dentro del flujo reactivo
        Mono<Long> idMono = Mono.just(serverRequest.pathVariable("id"))
                .map(Long::parseLong)
                .onErrorMap(e -> new IllegalAccessException("El ID del préstamo debe ser un número válido."));

        // 2. Procesamos el cuerpo de la petición y validamos el DTO
        Mono<UpdateLoanStatusRequest> dtoMono = serverRequest.bodyToMono(UpdateLoanStatusRequest.class)
               .switchIfEmpty(Mono.error(new IllegalArgumentException("El cuerpo de la petición es obligatorio")))
               .flatMap(requestValidator::validate);

        // 3. Obtenemos el usuario autenticado
        Mono<CustomPrincipal> principalMono = ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (CustomPrincipal) ctx.getAuthentication().getPrincipal());

        // 4. Combino y ejecuto la lógica.
        return Mono.zip(idMono, dtoMono, principalMono)
                .flatMap(tuple -> {
                    Long loanId = tuple.getT1();
                    UpdateLoanStatusRequest dto = tuple.getT2();

                    log.info("Procesando actualización para la solicitud #{} al nuevo estado {}.", loanId, dto.getNewStatus());

                    return approveOrRejectLoanInputPort.changeLoanStatus(loanId, dto.getNewStatus());
                })
                .flatMap(loanApplication -> ServerResponse.ok().bodyValue(loanApplication));



    }



}
