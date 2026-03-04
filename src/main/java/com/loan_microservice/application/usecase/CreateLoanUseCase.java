package com.loan_microservice.application.usecase;

import com.loan_microservice.domain.exception.CustomerNotFoundException;
import com.loan_microservice.domain.exception.MaxTermInMonthsException;
import com.loan_microservice.domain.exception.MinLoanAmountException;
import com.loan_microservice.domain.exception.MinTermInMonthsException;
import com.loan_microservice.application.ports.in.CreateLoanInputPort;
import com.loan_microservice.application.ports.out.LoanRepositoryOutPort;
import com.loan_microservice.application.ports.out.UserServiceOutPort;
import com.loan_microservice.domain.model.loan.LoanApplication;
import com.loan_microservice.domain.model.loan.LoanStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class CreateLoanUseCase implements CreateLoanInputPort {

    private final LoanRepositoryOutPort loanRepositoryOutPort;
    private final UserServiceOutPort userServiceOutPort;

    private static final BigDecimal MIN_LOAN_AMOUNT = new BigDecimal("1000000");

    public CreateLoanUseCase(LoanRepositoryOutPort loanRepositoryOutPort, UserServiceOutPort userServiceOutPort) {
        this.loanRepositoryOutPort = loanRepositoryOutPort;
        this.userServiceOutPort = userServiceOutPort;
    }

    @Override
    @Transactional
    public Mono<LoanApplication> create(LoanApplication loanApplication) {
        log.info("Procesando solicitud de préstamo para cliente DNI: {}", loanApplication.getCustomerDni());

        // Asignar automáticamente estado "Pendiente de revisión" y fecha de creación
        loanApplication.setLoanStatus(LoanStatus.PENDING_REVIEW);
        loanApplication.setCreatedAt(LocalDateTime.now());

        return validateCustomerExists(loanApplication.getCustomerDni())
                .then(validateBusinessRules(loanApplication))
                .flatMap(validLoan -> {
                    log.debug("Reglas de negocio validadas. Guardando en base de datos");
                    return loanRepositoryOutPort.save(validLoan);
                })
                .doOnSuccess(savedLoan -> log.info("Solicitud creada exitosamente con ID: {}", savedLoan.getId()))
                .doOnError(e -> log.error("Error al crear la solicitud de préstamo", e));
    }

        // Método para validar que el cliente exista en el microservicio de usuarios
    private Mono<Void> validateCustomerExists(String customerDni) {
        return userServiceOutPort.existsByDni(customerDni)
                .flatMap(exists -> {
                    if (!exists) {
                        log.warn("Solicitud rechazada: No se encontró un cliente con DNI: {}", customerDni);
                        return Mono.error(new CustomerNotFoundException(
                                "No se encontró un cliente con DNI: " + customerDni));
                    }
                    log.info("Cliente con DNI: {} verificado exitosamente", customerDni);
                    return Mono.empty();
                });
    }

        // Método propio para validar reglas de negocio (reglas no existentes en las HU pero que tienen lógica) (debería este método estar en una clase aparte)
    private Mono<LoanApplication> validateBusinessRules(LoanApplication loan){
        // Monto mínimo
        if(loan.getAmount().compareTo(MIN_LOAN_AMOUNT) < 0){
            log.warn("Solicitud rechazada: Monto {} es menor que el mínimo permitido {}", loan.getAmount(), MIN_LOAN_AMOUNT);
            return Mono.error(new MinLoanAmountException("El monto mínimo del préstamo debe ser de " + MIN_LOAN_AMOUNT));
        }

        // Plazo máximo
        if(loan.getTermInMonths() > 120){
            return Mono.error(new MaxTermInMonthsException("El plazo no puede exceder los 120 meses."));
        }

        // Plazo minimo
        if(loan.getTermInMonths() < 6){
            return Mono.error(new MinTermInMonthsException("El plazo no puede ser menor de 6 meses."));
        }
        return Mono.just(loan);
    }
}
