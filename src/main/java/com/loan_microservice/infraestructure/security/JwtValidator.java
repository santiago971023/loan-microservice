package com.loan_microservice.infraestructure.security;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtValidator {

    private final JwtProperties jwtProperties;

    public JwtValidator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private Key key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public Claims getClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
