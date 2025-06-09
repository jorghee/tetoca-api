package com.tetoca.tetoca_api.global.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G2M_INSTANCIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceEntity {

  @Id
  @Column(name = "InsEmpCod")
  private Long companyId;

  @Column(name = "InsTipBdCod", nullable = false)
  private Integer dbTypeCode;

  @Column(name = "InsEstConCod", nullable = false)
  private Integer connectionStatusCode;

  @Column(name = "InsNomBd", nullable = false)
  private String dbName;

  @Column(name = "InsUriCon", nullable = false)
  private String dbUri;

  @Column(name = "InsFecAct")
  private Integer lastActivatedDate;
}
