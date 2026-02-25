package com.loan_microservice.infraestructure.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {


    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String token = resolveToken(exchange);

        if (token != null ){
            return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
        } else {
            return Mono.empty();
        }
    }


    private String resolveToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
