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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
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
                                        .onErrorResume(e -> {
                                            if (e instanceof AuthServiceUnavailableException || e instanceof WebClientRequestException) {
                                                return Mono.just(User.builder().name("Servicio de usuarios no disponible").build());
                                            }
                                            if (e instanceof CustomerNotFoundException) {
                                                return Mono.just(User.builder().name("Cliente no registrado").build());
                                            }
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
