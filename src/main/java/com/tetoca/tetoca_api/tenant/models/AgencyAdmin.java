package com.tetoca.tetoca_api.tenant.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1C_ADMIN_AGENCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AgencyAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AdmAgeCod")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AdmAgeAgeCod", nullable = false)
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AdmAgeTraCod", nullable = false)
    private Worker worker;

    @Column(name = "AdmAgeFecAsig", nullable = false)
    private Integer assignmentDate;

    @Column(name = "AdmAgeEstReg", nullable = false, length = 1)
    private String recordStatus = "A";
}
