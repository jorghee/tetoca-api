package com.tetoca.tetoca_api.tenant.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A2M_COLA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ColCod")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ColTipColCod", nullable = false)
    private QueueType queueType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ColAgeCod", nullable = false)
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ColEstColCod", nullable = false)
    private QueueStatus queueStatus;

    @Column(name = "ColNom", nullable = false, length = 100)
    private String name;

    @Column(name = "ColCapMax")
    private Integer maxCapacity;

    @Column(name = "ColTieEst")
    private Integer estimatedTimePerTurn;

    @Column(name = "ColEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
