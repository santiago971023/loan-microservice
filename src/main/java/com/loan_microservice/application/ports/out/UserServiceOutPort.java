package com.loan_microservice.application.ports.out;

import com.loan_microservice.domain.model.user.User;
import reactor.core.publisher.Mono;

public interface UserServiceOutPort {

    Mono<Boolean> existsByDni(String dni);
    Mono<User> findUserByDni(String dni);

}
