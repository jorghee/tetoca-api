package com.tetoca.tetoca_api.global.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G1M_EMPRESA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEntity {

  @Id
  @Column(name = "EmpCodEmp")
  private Long id;

  @Column(name = "EmpNom", nullable = false)
  private String commercialName;

  @Column(name = "EmpRuc", nullable = false)
  private String taxId;

  @Column(name = "EmpCorEle", nullable = false)
  private String email;

  @Column(name = "EmpEstEmpCod", nullable = false)
  private Integer statusCode;

  @Column(name = "EmpFecReg")
  private Integer registrationDate;

  @Column(name = "EmpObs")
  private String notes;
}
