package com.tetoca.tetoca_api.global.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZZ_TIPO_EVENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventTypeEntity {

  @Id
  @Column(name = "TipEveCod")
  private Integer id;

  @Column(name = "TipEveNom", nullable = false, length = 30)
  private String name;

  @Column(name = "TipEveDes", length = 100)
  private String description;

  @Column(name = "TipEveEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
