package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.tenant.dto.QueueResponse;
import com.tetoca.tetoca_api.tenant.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueueController {
    
    private final QueueService queueService;
    
    @GetMapping("/enterprises/{enterpriseId}/queues")
    public ResponseEntity<Map<String, Object>> getQueuesByEnterprise(@PathVariable Integer enterpriseId) {
        List<QueueResponse> queues = queueService.getQueuesByAgencyId(enterpriseId);
        return ResponseEntity.ok(Map.of("data", queues));
    }
    
    @GetMapping("/queues/{queueId}")
    public ResponseEntity<QueueResponse> getQueueById(@PathVariable Integer queueId) {
        QueueResponse queue = queueService.getQueueById(queueId);
        return ResponseEntity.ok(queue);
    }
}