package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AZZ_ESTADO_COLA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class QueueStatus {

    @Id
    @Column(name = "EstColCod")
    private Integer id;

    @Column(name = "EstColNom", nullable = false, length = 50)
    private String name;

    @Column(name = "EstColDes", length = 255)
    private String description;

    @Column(name = "EstColEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
