package com.tetoca.tetoca_api.tenant.controller;

import com.tetoca.tetoca_api.tenant.dto.TicketResponse;
import com.tetoca.tetoca_api.tenant.service.TurnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {
    
    private final TurnService turnService;
    
    @PostMapping("/queues/{queueId}/join")
    public ResponseEntity<TicketResponse> joinQueue(
            @PathVariable Integer queueId,
            @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        
        TicketResponse ticket = turnService.joinQueue(queueId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
    
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketDetails(@PathVariable String ticketId) {
        TicketResponse ticket = turnService.getTurnDetails(ticketId);
        return ResponseEntity.ok(ticket);
    }
    
    @PutMapping("/tickets/{ticketId}/pause")
    public ResponseEntity<TicketResponse> pauseTicket(@PathVariable String ticketId) {
        TicketResponse ticket = turnService.pauseTurn(ticketId);
        return ResponseEntity.ok(ticket);
    }
    
    @PutMapping("/tickets/{ticketId}/resume")
    public ResponseEntity<TicketResponse> resumeTicket(@PathVariable String ticketId) {
        TicketResponse ticket = turnService.resumeTurn(ticketId);
        return ResponseEntity.ok(ticket);
    }
    
    @DeleteMapping("/tickets/{ticketId}/cancel")
    public ResponseEntity<Map<String, String>> cancelTicket(
            @PathVariable String ticketId,
            @RequestParam(required = false) String description) {
        
        turnService.cancelTurn(ticketId, description);
        return ResponseEntity.ok(Map.of("message", "Ticket cancelado exitosamente"));
    }
}