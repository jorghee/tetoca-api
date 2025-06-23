package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G1M_ADMIN_SAAS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaaSAdmin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "AdmSaaCod")
  private Integer id;

  @Column(name = "AdmSaaNom", nullable = false, length = 100)
  private String fullName;

  @Column(name = "AdmSaaCorEle", nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "AdmSaaCla", nullable = false, length = 255)
  private String password;

  @Column(name = "AdmSaaFecReg", nullable = false)
  private Integer registerDate;

  @Builder.Default
  @Column(name = "AdmSaaEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
