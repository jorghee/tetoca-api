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
  @OneToOne
  @JoinColumn(name = "InsEmpCod", nullable = false)
  private CompanyEntity company;

  @ManyToOne
  @JoinColumn(name = "InsEstConCod", nullable = false)
  private ConnectionStateEntity connectionState;

  @ManyToOne
  @JoinColumn(name = "InsTipBdCod", nullable = false)
  private DatabaseTypeEntity dbType;

  @Column(name = "InsNomBd", nullable = false, length = 50)
  private String dbName;

  @Column(name = "InsUriCon", nullable = false, length = 255)
  private String dbUri;

  @Column(name = "InsUsuBd", nullable = false, length = 100)
  private String dbUsername;

  @Column(name = "InsClaBd", nullable = false, length = 100)
  private String dbPassword;

  @Column(name = "InsFecAct")
  private Integer lastActiveDate;

  @Column(name = "InsEstReg", nullable = false, length = 1)
  private String recordState; 
}
