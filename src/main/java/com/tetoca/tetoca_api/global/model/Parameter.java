package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZC_PARAMETRO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parameter {

  @Id
  @Column(name = "ParCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ParTipParCod", nullable = false)
  private ParameterType parameterType;

  @Column(name = "ParNom", nullable = false, length = 100)
  private String name;

  @Column(name = "ParVal", nullable = false, length = 255)
  private String value;

  @Column(name = "ParFecAct", nullable = false)
  private Integer updateDate;

  @Builder.Default
  @Column(name = "ParEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
