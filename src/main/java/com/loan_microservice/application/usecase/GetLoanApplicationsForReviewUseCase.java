package com.loan_microservice.application.usecase;

import com.loan_microservice.application.ports.in.GetLoanApplicationsForReviewInputPort;
import com.loan_microservice.application.ports.out.LoanRepositoryOutPort;
import com.loan_microservice.application.ports.out.UserServiceOutPort;
import com.loan_microservice.domain.exception.AuthServiceUnavailableException;
import com.loan_microservice.domain.exception.CustomerNotFoundException;
import com.loan_microservice.domain.model.loan.LoanApplication;
import com.loan_microservice.domain.model.loan.LoanApplicationDetail;
import com.loan_microservice.domain.model.loan.LoanStatus;
import com.loan_microservice.domain.model.pageable.DomainPageable;
import com.loan_microservice.domain.model.pageable.PaginationResponse;
import com.loan_microservice.domain.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class GetLoanApplicationsForReviewUseCase implements GetLoanApplicationsForReviewInputPort {

    private final LoanRepositoryOutPort loanRepositoryOutPort;
    private final UserServiceOutPort userServiceOutPort;

    public GetLoanApplicationsForReviewUseCase(LoanRepositoryOutPort loanRepositoryOutPort, UserServiceOutPort userServiceOutPort) {
        this.loanRepositoryOutPort = loanRepositoryOutPort;
        this.userServiceOutPort = userServiceOutPort;
    }


    @Override
    public Mono<PaginationResponse<LoanApplicationDetail>> getLoanApps(List<LoanStatus> statuses, DomainPageable domainPageable) {

        return loanRepositoryOutPort.findLoanByStatus(statuses, domainPageable)
                .flatMap(pageRespone -> {

                    // Pagina con loans crudos
                    List<LoanApplication> loans = pageRespone.getData();

                    // Convierto la lista a flujo
                    return Flux.fromIterable(loans)
                            .flatMap(loan -> {
                                return userServiceOutPort.findUserByDni(loan.getCustomerDni())
                                        .doFirst( () -> log.info("Buscando al usuario con DNI {}, para la solicitud {}", loan.getCustomerDni(), loan.getId()))
                                        .onErrorResume(e -> {
                                            if (e instanceof AuthServiceUnavailableException || e instanceof WebClientRequestException) {
                                                log.warn("Resiliencia activada: el servicio de usuarios no responde para la solicitud {}. Motivo: {}", loan.getId(), e.getMessage());
                                                return Mono.just(User.builder().name("Servicio de usuarios no disponible").build());
                                            }
                                            if (e instanceof CustomerNotFoundException) {
                                                log.info("Resiliencia activada: el cliente con DNI {} no existe en auth-service.", loan.getCustomerDni());
                                                return Mono.just(User.builder().name("Cliente no registrado").build());
                                            }
                                            log.error("Error inesperado en el flujo de usuario para la solicitud {}: ", loan.getId(), e);
                                            return Mono.error(e);
                                        })
                                        .map(user -> LoanApplicationDetail.builder()
                                                .loanApplication(loan)
                                                .user(user)
                                                .build());
                            })
                            .collectList()
                            .map(detailsList -> new PaginationResponse<>(
                                    detailsList,
                                    pageRespone.getTotalElements(),
                                    pageRespone.getDomainPageable()
                            ));

                });
    }
}
