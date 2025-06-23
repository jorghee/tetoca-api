package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G1M_EMPRESA")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "EmpCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EmpEstEmpCod", nullable = false)
  private CompanyState companyState;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EmpCatEmpCod", nullable = false)
  private CompanyCategory companyCategory;

  @Column(name = "EmpNom", nullable = false, length = 100)
  private String name;

  @Column(name = "EmpRuc", nullable = false, length = 11, unique = true)
  private String ruc;

  @Column(name = "EmpCorEle", nullable = false, length = 100)
  private String email;

  @Column(name = "EmpFecReg", nullable = false)
  private Integer registerDate;

  @Column(name = "EmpObs", length = 255)
  private String notes;

  @Builder.Default
  @Column(name = "EmpEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
