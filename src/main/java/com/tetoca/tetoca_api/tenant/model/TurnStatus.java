package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AZZ_ESTADO_TURNO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TurnStatus {

    @Id
    @Column(name = "EstTurCod")
    private Integer id;

    @Column(name = "EstTurNom", nullable = false, length = 50)
    private String name;

    @Column(name = "EstTurDes", length = 255)
    private String description;

    @Column(name = "EstTurEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
