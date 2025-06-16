package com.tetoca.tetoca_api.global.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G1M_CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientEntity {

  @Id
  @Column(name = "CliCod")
  private Integer id;

  @Column(name = "CliNom", nullable = false, length = 100)
  private String name;

  @Column(name = "CliCorEle", nullable = false, length = 100)
  private String email;

  @Column(name = "CliUidExt", nullable = false, length = 100)
  private String externalUid;

  @Column(name = "CliProAut", nullable = false, length = 20)
  private String authProvider;

  @Column(name = "CliNumTel", length = 15)
  private String phone;

  @Column(name = "CliFecReg", nullable = false)
  private Integer registerDate;

  @Builder.Default
  @Column(name = "CliEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
