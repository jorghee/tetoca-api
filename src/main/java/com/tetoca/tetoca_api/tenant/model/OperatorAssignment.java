package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A2C_ASIGNACION_OPERARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperatorAssignment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "AsgCodAsg")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AsgColCod", nullable = false)
  private Queue queue;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AsgOpeTraCod", nullable = false)
  private Operator operator;

  @Column(name = "AsgFecIni")
  private Integer startDate;

  @Column(name = "AsgFecFin")
  private Integer endDate;

  @Column(name = "AsgEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
