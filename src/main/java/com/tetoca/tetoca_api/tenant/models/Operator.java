package com.tetoca.tetoca_api.tenant.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1C_OPERARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Operator {

    @Id
    @Column(name = "OpeTraCod")
    private Integer workerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OpeTraCod")
    @MapsId
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OpeAgeCod", nullable = false)
    private Agency agency;

    @Column(name = "OpeEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
