package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1M_TRABAJADOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TraCod")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TraTipTraCod", nullable = false)
    private WorkerType workerType;

    @Column(name = "TraNom", nullable = false, length = 100)
    private String fullName;

    @Column(name = "TraCorEle", nullable = false, length = 100)
    private String email;

    @Column(name = "TraCla", nullable = false, length = 255)
    private String password;

    @Column(name = "TraTel", length = 15)
    private String phone;

    @Column(name = "TraEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
