package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G2M_INSTANCIA")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instance {

  @Id
  @Column(name = "InsEmpCod")
  private Integer companyId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "InsEmpCod")
  private Company company;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "InsEstConCod", nullable = false)
  private ConnectionState connectionState;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "InsTipBdCod", nullable = false)
  private DatabaseType dbType;

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
