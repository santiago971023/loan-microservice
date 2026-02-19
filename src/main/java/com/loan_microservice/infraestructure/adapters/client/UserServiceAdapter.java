package com.loan_microservice.infraestructure.adapters.client;

import com.loan_microservice.application.ports.out.UserServiceOutPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceAdapter implements UserServiceOutPort {

    private final WebClient webClient;

    public UserServiceAdapter(WebClient.Builder webClientBuilder,
                             @Value("${services.user-service.url}") String userServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    @Override
    public Mono<Boolean> existsByDni(String dni) {
        return webClient.get()
                .uri("/users/exists/{dni}", dni)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }
}
