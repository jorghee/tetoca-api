package com.tetoca.tetoca_api.company.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AZZ_TIPO_TRABAJADOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class WorkerType {

    @Id
    @Column(name = "TipTraCod")
    private Integer id;

    @Column(name = "TipTraNom", nullable = false, length = 50)
    private String name;

    @Column(name = "TipTraDes", length = 255)
    private String description;

    @Column(name = "TipTraEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
