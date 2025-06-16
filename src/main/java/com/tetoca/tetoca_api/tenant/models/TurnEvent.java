package com.tetoca.tetoca_api.tenant.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A3H_EVENTO_TURNO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TurnEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EveTurCod")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EveTurTurCod", nullable = false)
    private Turn turn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EveTurAntEstTurCod")
    private TurnStatus previousStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EveTurNueEstTurCod", nullable = false)
    private TurnStatus newStatus;

    @Column(name = "EveTurFecHor", nullable = false)
    private Long eventDateTime;

    @Column(name = "EveTurMot", length = 150)
    private String reason;

    @Column(name = "EveTurEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
