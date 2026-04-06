package com.loan_microservice.application.ports.out;

import com.loan_microservice.domain.model.notification.NotificationData;
import reactor.core.publisher.Mono;

public interface SendNotificationOutputPort {
    Mono<Void> sendNotification(NotificationData data);
}
