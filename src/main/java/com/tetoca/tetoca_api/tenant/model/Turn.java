package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A3T_TURNO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Turn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TurCod")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TurRegColCod", nullable = false)
    private QueueRegistration queueRegistration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TurEstTurCod", nullable = false)
    private TurnStatus turnStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TurOpeTraCod")
    private Operator operator;

    @Column(name = "TurNumOrd", nullable = false)
    private Integer orderNumber;

    @Column(name = "TurFecHorGen", nullable = false)
    private Long generationDateTime;

    @Column(name = "TurFecHorAte")
    private Long attentionDateTime;

    @Column(name = "TurEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
