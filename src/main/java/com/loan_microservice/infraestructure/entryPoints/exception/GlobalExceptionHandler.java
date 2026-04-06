package com.loan_microservice.infraestructure.entryPoints.exception;

import com.loan_microservice.domain.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFound(CustomerNotFoundException ex) {
        log.warn("Cliente no encontrado: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ImpersonationNotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleImpersonationNotAllowed(ImpersonationNotAllowedException ex){
        log.warn("No puede crear una solicitud para otra persona.");
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidLoanTypeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidLoanType(InvalidLoanTypeException ex) {
        log.warn("Tipo de préstamo inválido: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MinLoanAmountException.class)
    public ResponseEntity<Map<String, Object>> handleMinLoanAmount(MinLoanAmountException ex) {
        log.warn("Monto mínimo no cumplido: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MaxTermInMonthsException.class)
    public ResponseEntity<Map<String, Object>> handleMaxTermInMonths(MaxTermInMonthsException ex) {
        log.warn("Plazo máximo excedido: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MinTermInMonthsException.class)
    public ResponseEntity<Map<String, Object>> handleMinTermInMonths(MinTermInMonthsException ex) {
        log.warn("Plazo mínimo no cumplido: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(WebExchangeBindException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Error de validación: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedRequest(IllegalStateException ex){
        log.warn("Error de autorización: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AuthServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleAuthServiceUnavailable(AuthServiceUnavailableException ex){
        log.warn("El Auth Microservice no se encuentra disponible en este momento.");
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedRequest(ConstraintViolationException ex){
        log.warn("Error en la validación de datos de entrada: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAnythingElse(Exception ex){
        log.error("Error inesperado: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado en el servidor, por favor contacte con soporte.");
    }


    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
