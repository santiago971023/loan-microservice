package com.loan_microservice.infraestructure.adapters.client;

import com.loan_microservice.application.ports.out.UserServiceOutPort;
import com.loan_microservice.domain.exception.AuthServiceUnavailableException;
import com.loan_microservice.domain.exception.CustomerNotFoundException;
import com.loan_microservice.domain.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
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
                .uri("/exists/{dni}", dni)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    @Override
    public Mono<User> findUserByDni(String dni) {
        return ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                // Validamos seguridad primero
                        .switchIfEmpty(Mono.error(new IllegalStateException("No hay nadie autenticado.")))
                        .flatMap(authentication -> {
                            String token = authentication.getCredentials().toString();
                            return webClient.get()
                                    .uri("/{dni}", dni)
                                    .header("Authorization", "Bearer " + token)
                                    .retrieve()
                                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                                            Mono.error(new CustomerNotFoundException("No encontramos al cliente con DNI: " + dni)))
                                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                                            Mono.error(new AuthServiceUnavailableException("El servicio de usuarios está teniendo problemas.")))
                                    .bodyToMono(User.class);
                                });


    }

}
