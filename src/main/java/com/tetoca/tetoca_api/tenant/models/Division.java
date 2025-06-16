package com.tetoca.tetoca_api.tenant.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1M_DIVISION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DivCod")
    private Integer id;

    @Column(name = "DivNom", nullable = false, length = 100)
    private String name;

    @Column(name = "DivDes", length = 255)
    private String description;

    @Column(name = "DivEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
