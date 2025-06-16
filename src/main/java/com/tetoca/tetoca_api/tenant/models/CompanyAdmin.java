package com.tetoca.tetoca_api.tenant.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1C_ADMIN_EMPRESA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CompanyAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AdmEmpCod")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AdmEmpTraCod", nullable = false)
    private Worker worker;

    @Column(name = "AdmEmpFecAsig", nullable = false)
    private Integer assignmentDate;

    @Column(name = "AdmEmpEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
