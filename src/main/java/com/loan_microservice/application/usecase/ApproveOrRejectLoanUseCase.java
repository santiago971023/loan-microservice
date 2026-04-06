package com.loan_microservice.application.usecase;

import com.loan_microservice.application.ports.in.ApproveOrRejectLoanInputPort;
import com.loan_microservice.application.ports.out.LoanRepositoryOutPort;
import com.loan_microservice.application.ports.out.SendNotificationOutputPort;
import com.loan_microservice.application.ports.out.UserServiceOutPort;
import com.loan_microservice.domain.model.loan.LoanApplication;
import com.loan_microservice.domain.model.loan.LoanStatus;
import com.loan_microservice.domain.model.notification.NotificationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ApproveOrRejectLoanUseCase implements ApproveOrRejectLoanInputPort {

    private final LoanRepositoryOutPort loanRepositoryOutPort;
    private final UserServiceOutPort userServiceOutPort;
    private final SendNotificationOutputPort sendNotificationOutputPort;

    public ApproveOrRejectLoanUseCase(LoanRepositoryOutPort loanRepositoryOutPort, UserServiceOutPort userServiceOutPort, SendNotificationOutputPort sendNotificationOutputPort) {
        this.loanRepositoryOutPort = loanRepositoryOutPort;
        this.userServiceOutPort = userServiceOutPort;
        this.sendNotificationOutputPort = sendNotificationOutputPort;
    }


    @Override
    public Mono<LoanApplication> changeLoanStatus(Long loanId, String newStatus) {
        return loanRepositoryOutPort.findLoanApplicationById(loanId)
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró una solicitud con ese ID")))
                .flatMap(loan -> userServiceOutPort.findUserByDni(loan.getCustomerDni())
                        .doFirst( () -> log.info("Se procede a validar el nuevo estado: {}.", newStatus) )
                    .flatMap(user -> {
                        LoanStatus loanStatusEnum = validateAndParseStatus(newStatus);
                        loan.setLoanStatus(loanStatusEnum);
                        log.info("El nuevo estado de la solicitud se modificó correctamente. Procedemos con la construcción del cuerpo de la notificación.");
                        NotificationData notificationData = NotificationData.builder()
                                .loanId(loanId)
                                .customerName(user.getName())
                                .loanStatus(newStatus)
                                .customerEmail(user.getEmail())
                                .build();
                        log.info("Después de construído el cuerpo de la nofiticación, se procede con el guardado en bbdd.");
                        return loanRepositoryOutPort.save(loan)
                                .doOnSuccess(saved -> log.info("Préstamo ID {} guardado con éxito en la DB. Enviando notificación...", loanId) )
                                .flatMap(savedLoan -> sendNotificationOutputPort.sendNotification(notificationData)
                                        .thenReturn(savedLoan)
                                );
                    })
                );

    }


    private LoanStatus validateAndParseStatus(String status) {
        try {
            return LoanStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + status);
        }
    }



}


/*
* 📝 [HU #06] - Aprobar o rechazar solicitud de crédito

  Objetivo: Permitir que un asesor apruebe/rechace préstamos y notificar al cliente vía SQS -> Lambda -> SNS.

  Estado Actual: ✅ Lógica de Dominio y Aplicación Finalizada.
   1. Salida (Persistencia): LoanRepositoryOutPort y su adaptador ya tienen findLoanApplicationById.
   2. Mensajería: Creado NotificationData (Dominio) y SendNotificationOutputPort (Puerto de salida).
   3. Caso de Uso: ApproveOrRejectLoanUseCase implementado con flujo reactivo completo (Validación de Enum, Búsqueda de Usuario para email, Guardado en DB y Envío de Notificación con Logs de traza).

  Siguientes Pasos (Paso 5 y 6):
   1. DTO de Entrada: Crear UpdateLoanStatusRequest para recibir loanId y newStatus.
   2. Handler: Implementar el método en LoanHandler para procesar el PUT y llamar al Use Case.
   3. Router: Mapear la ruta PUT /api/v1/solicitud en LoanRouter.
   4. Seguridad: Configurar el rol permitido (Asesor/Consultant) en SecurityConfig.java.
   5. Infraestructura AWS: (Opcional/Pendiente) Crear el adaptador que implemente SendNotificationOutputPort usando el cliente de SQS.

  ---

  Comentario Sugerido para el código:

   1 /*
   2  * TODO: HU #06 - PASO 5: IMPLEMENTAR HANDLER Y ROUTER
   3  * El Use Case 'ApproveOrRejectLoanUseCase' ya está listo con toda la lógica reactiva.
   4  * Mañana debemos:
   5  * 1. Crear DTO para el cuerpo de la petición.
   6  * 2. Agregar lógica al LoanHandler.
   7  * 3. Configurar la ruta en LoanRouter.
   8  */



