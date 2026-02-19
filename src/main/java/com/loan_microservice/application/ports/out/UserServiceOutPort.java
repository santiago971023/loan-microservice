package com.loan_microservice.application.ports.out;

import reactor.core.publisher.Mono;

public interface UserServiceOutPort {

    Mono<Boolean> existsByDni(String dni);

}
