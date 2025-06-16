package com.tetoca.tetoca_api.tenant.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A3H_EVENTO_COLA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class QueueEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EveColCod")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EveColColCod", nullable = false)
    private Queue queue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EveColAntEstColCod")
    private QueueStatus previousStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EveColNueEstColCod", nullable = false)
    private QueueStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EveColTraCod", nullable = false)
    private Worker worker;

    @Column(name = "EveColFecHor", nullable = false)
    private Long eventDateTime;

    @Column(name = "EveColMot", length = 150)
    private String reason;

    @Column(name = "EveColEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
