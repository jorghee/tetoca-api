package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G3C_AUDITORIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Audit {

  @Id
  @Column(name = "AudCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AudTipAccCod", nullable = false)
  private ActionType actionType;

  @Column(name = "AudFecHor", nullable = false)
  private Long dateTime;

  @Column(name = "AudUsuAdm", nullable = false, length = 50)
  private String adminUser;

  @Column(name = "AudIpCli", nullable = false, length = 15)
  private String clientIp;

  @Column(name = "AudObs", length = 255)
  private String observation;

  @Builder.Default
  @Column(name = "AudEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
