package com.tetoca.tetoca_api.company.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1M_CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CliCod")
    private Integer id;

    @Column(name = "CliUidExt", length = 100)
    private String externalUid;

    @Column(name = "CliNom", nullable = false, length = 100)
    private String fullName;

    @Column(name = "CliCorEle", length = 100)
    private String email;

    @Column(name = "CliFecReg")
    private Integer registrationDate;

    @Column(name = "CliEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
