package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A2M_HORARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "HorCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "HorColCod", nullable = false)
  private Queue queue;

  @Column(name = "HorDia", nullable = false)
  private Integer dayOfWeek;

  @Column(name = "HorHorIni", nullable = false)
  private Integer startTime;

  @Column(name = "HorHorFin", nullable = false)
  private Integer endTime;

  @Column(name = "HorEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
