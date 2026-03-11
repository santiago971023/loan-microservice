package com.loan_microservice.infraestructure.entryPoints;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class LoanRouter {

    private static final String PATH = "/api/v1/loans";

    @Bean
    public RouterFunction<ServerResponse> loanRoutes(LoanHandler loanHandler) {
        return route(
                POST(PATH)
                        .and(accept(MediaType.APPLICATION_JSON)),
                loanHandler::createLoan
        ).andRoute(
            GET(PATH + "/reviews")
                    .and(accept(MediaType.APPLICATION_JSON)),
                loanHandler::listLoanApps
        );
    }
}
