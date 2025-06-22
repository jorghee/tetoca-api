package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZZ_CATEGORIA_EMPRESA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCategory {

  @Id
  @Column(name = "CatEmpCod")
  private Integer id;

  @Column(name = "CatEmpNom", nullable = false, unique = true, length = 30)
  private String name;

  @Column(name = "CatEmpDes", length = 100)
  private String description;

  @Column(name = "CatEmpEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
