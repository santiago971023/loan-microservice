package com.loan_microservice.infraestructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtValidator jwtValidator;

    public JwtAuthenticationManager(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication.getCredentials().toString())
                .map(token -> {
                    // Extraigo Claims
                    Claims claims = jwtValidator.getClaimsFromToken(token);

                    // Extraigo los datos
                    String email = claims.get("email", String.class);
                    String role = claims.get("role", String.class);
                    Long userId = claims.get("userId", Long.class);
                    String dni = claims.get("dni", String.class);

                    // Validación
                    if(userId == null || role == null || email == null || dni == null){
                        log.warn("Token rechazado: Faltan Claims obligatorios.");
                        throw new BadCredentialsException("Token Incompleto.");
                    }

                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    CustomPrincipal principal = new CustomPrincipal(userId, dni, email, token);
                    return (Authentication) new UsernamePasswordAuthenticationToken(principal, token, List.of(authority));
                })
                .onErrorResume(e -> {
                    log.error("Error validando JWT: {}", e.getMessage());
                    return Mono.empty();
                });
    }
}
