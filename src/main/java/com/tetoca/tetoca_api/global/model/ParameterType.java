package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZZ_TIPO_PARAMETRO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterType {

  @Id
  @Column(name = "TipParCod")
  private Integer id;

  @Column(name = "TipParNom", nullable = false, length = 30)
  private String name;

  @Column(name = "TipParDes", length = 100)
  private String description;

  @Column(name = "TipParEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
