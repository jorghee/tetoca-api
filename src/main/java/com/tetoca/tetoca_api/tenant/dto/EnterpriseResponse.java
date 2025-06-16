package com.tetoca.tetoca_api.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseResponse {
    private String id;
    private String name;
    private String shortName;
    private String type;
    private String logo;
    private String address;
    private String schedule;
    private String phone;
    private Boolean isAvailable;
    private Integer activeQueues;
    private List<QueueResponse> queues;
}