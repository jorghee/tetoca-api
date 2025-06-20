package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1C_ADMIN_DIVISION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DivisionAdmin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "AdmDivCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AdmDivDivCod", nullable = false)
  private Division division;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AdmDivTraCod", nullable = false)
  private Worker worker;

  @Column(name = "AdmDivFecAsig", nullable = false)
  private Integer assignmentDate;

  @Column(name = "AdmDivEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
