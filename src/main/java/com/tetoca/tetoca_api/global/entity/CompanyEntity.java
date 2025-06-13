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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "EmpCod")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "EmpEstEmpCod", nullable = false)
  private CompanyStateEntity state;

  @Column(name = "EmpNom", nullable = false, length = 100)
  private String name;

  @Column(name = "EmpRuc", nullable = false, length = 11, unique = true)
  private String ruc;

  @Column(name = "EmpCorEle", nullable = false, length = 100)
  private String email;

  @Column(name = "EmpFecReg", nullable = false)
  private Integer registrationDate;

  @Column(name = "EmpObs", length = 255)
  private String notes;

  @Column(name = "EmpEstReg", nullable = false, length = 1)
  private String recordState;
}
