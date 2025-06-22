package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZZ_ESTADO_EMPRESA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyState {

  @Id
  @Column(name = "EstEmpCod")
  private Integer id;

  @Column(name = "EstEmpNom", nullable = false, length = 30)
  private String name;

  @Column(name = "EstEmpDes", length = 100)
  private String description;

  @Column(name = "EstEmpEstReg", nullable = false, length = 1)
  private String recordState = "A";
}
