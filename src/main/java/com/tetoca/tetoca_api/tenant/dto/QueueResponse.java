package com.tetoca.tetoca_api.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueResponse {
    private String id;
    private String name;
    private String icon;
    private Integer peopleWaiting;
    private String avgTime;
    private String enterpriseId;
    private Boolean isActive;
    private String currentTicket;
    private String waitTimePerPerson;
    private EnterpriseResponse enterprise;
}