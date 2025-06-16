package com.tetoca.tetoca_api.company.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AZZ_TIPO_COLA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class QueueType {

    @Id
    @Column(name = "TipColCod")
    private Integer id;

    @Column(name = "TipColNom", nullable = false, length = 50)
    private String name;

    @Column(name = "TipColDes", length = 255)
    private String description;

    @Column(name = "TipColEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
