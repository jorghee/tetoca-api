package com.tetoca.tetoca_api.tenant.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A2T_REGISTRO_COLA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class QueueRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RegColCod")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RegColColCod", nullable = false)
    private Queue queue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RegColCliCod", nullable = false)
    private Client client;

    @Column(name = "RegColFecHor", nullable = false)
    private Long registrationDateTime;

    @Column(name = "RegColMet", length = 10)
    private String registrationMethod;

    @Column(name = "RegColEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
