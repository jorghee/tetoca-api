package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZZ_TIPO_DB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseType {

  @Id
  @Column(name = "TipBdCod")
  private Integer id;

  @Column(name = "TipBdNom", nullable = false, length = 30)
  private String name;

  @Column(name = "TipBdDes", length = 100)
  private String description;

  @Column(name = "TipBdEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
