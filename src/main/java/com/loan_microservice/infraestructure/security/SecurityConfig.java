package com.loan_microservice.infraestructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final JwtAuthenticationManager jwtAuthenticationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){

        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        jwtFilter.setServerAuthenticationConverter(jwtAuthenticationConverter);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .authorizeExchange(exchanges -> exchanges
                        // Rutas públicas


                        // Rutas protegidas por role
                        .pathMatchers("/api/v1/loans").hasAnyRole("CUSTOMER")
                                .pathMatchers("/api/v1/loans/reviews").hasAnyRole("CONSULTANT")
                                .pathMatchers("/api/v1/loans/{id}/newStatus").hasAnyRole("CONSULTANT")

                        // Rutas no especificadas.
                        .anyExchange().authenticated()
                        )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();

    }


}
