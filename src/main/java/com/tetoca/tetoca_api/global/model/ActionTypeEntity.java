package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZZ_TIPO_ACCION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionTypeEntity {

  @Id
  @Column(name = "TipAccCod")
  private Integer id;

  @Column(name = "TipAccNom", nullable = false, length = 30)
  private String name;

  @Column(name = "TipAccDes", length = 100)
  private String description;

  @Column(name = "TipAccEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
