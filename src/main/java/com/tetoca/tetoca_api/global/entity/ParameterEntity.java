package com.tetoca.tetoca_api.global.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZC_PARAMETRO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterEntity {

  @Id
  @Column(name = "ParCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ParTipParCod", nullable = false)
  private ParameterTypeEntity parameterType;

  @Column(name = "ParNom", nullable = false, length = 100)
  private String name;

  @Column(name = "ParVal", nullable = false, length = 255)
  private String value;

  @Column(name = "ParFecAct", nullable = false)
  private Integer updateDate;

  @Column(name = "ParEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
