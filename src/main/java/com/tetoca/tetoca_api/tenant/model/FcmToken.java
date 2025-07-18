package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1C_TOKEN_FCM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FcmToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "TokCod")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "TokCliEmpCod", nullable = false)
  private CompanyClient companyClient;

  @Column(name = "TokVal", nullable = false, length = 255)
  private String token;

  @Column(name = "TokFecReg", nullable = false)
  private Long registrationDateTime;

  @Column(name = "TokEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
