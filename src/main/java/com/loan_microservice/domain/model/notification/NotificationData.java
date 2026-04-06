package com.loan_microservice.domain.model.notification;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NotificationData {

    private String customerName;
    private String customerEmail;
    private Long loanId;
    private String loanStatus;

}
