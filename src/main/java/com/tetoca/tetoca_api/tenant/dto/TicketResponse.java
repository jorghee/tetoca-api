package com.tetoca.tetoca_api.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private String id;
    private String ticketId;
    private String queueId;
    private String enterpriseId;
    private String enterpriseName;
    private String queueName;
    private String issueDate;
    private String issueTime;
    private String status;
    private Integer position;
    private String currentTicket;
    private String waitTime;
    private String peopleTime;
    private String message;
}