package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1M_CLIENTE_EMPRESA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyClient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CliEmpCod")
  private Integer id;

  @Column(name = "CliEmpUidExt", length = 100)
  private String externalUid;

  @Column(name = "CliEmpNom", nullable = false, length = 100)
  private String fullName;

  @Column(name = "CliEmpCorEle", length = 100)
  private String email;

  @Column(name = "CliEmpFecReg")
  private Integer registrationDate;

  @Column(name = "CliEmpEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
