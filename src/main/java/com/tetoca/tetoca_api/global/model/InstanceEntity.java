package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G2M_INSTANCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "InsCod")
  private Integer id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "InsEmpCod", nullable = false)
  private CompanyEntity company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "InsEstConCod", nullable = false)
  private ConnectionStateEntity connectionState;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "InsTipBdCod", nullable = false)
  private DatabaseTypeEntity dbType;

  @Column(name = "InsNomBd", nullable = false, length = 50)
  private String dbName;

  @Column(name = "InsUriCon", nullable = false, length = 255)
  private String dbUri;

  @Column(name = "InsUsuBd", nullable = false, length = 100)
  private String dbUser;

  @Column(name = "InsClaBd", nullable = false, length = 100)
  private String dbPassword;

  @Column(name = "InsTenId", nullable = false, unique = true, length = 50)
  private String tenantId;

  @Column(name = "InsFecAct")
  private Integer lastActivationDate;

  @Builder.Default
  @Column(name = "InsEstReg", nullable = false, length = 1)
  private String recordStatus = "A"; 
}
