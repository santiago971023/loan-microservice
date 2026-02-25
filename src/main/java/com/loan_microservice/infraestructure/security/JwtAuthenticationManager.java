package com.loan_microservice.infraestructure.security;

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
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtValidator jwtValidator;

    public JwtAuthenticationManager(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication.getCredentials().toString())
                .map(token -> {
                    Claims claims = jwtValidator.getClaimsFromToken(token);
                    String email = claims.get("email", String.class);
                    Date expiry = claims.getExpiration();
                    String role = claims.get("role", String.class);
                    Long userId = claims.get("userId", Long.class);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    CustomPrincipal principal = new CustomPrincipal(userId, email);
                    return (Authentication) new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));
                })
                .onErrorResume(e -> Mono.empty());
    }
}
