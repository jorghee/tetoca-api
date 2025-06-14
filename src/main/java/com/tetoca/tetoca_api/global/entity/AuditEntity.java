package com.tetoca.tetoca_api.global.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G3C_AUDITORIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntity {

  @Id
  @Column(name = "AudCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AudTipAccCod", nullable = false)
  private ActionTypeEntity actionType;

  @Column(name = "AudFecHor", nullable = false)
  private Long dateTime;

  @Column(name = "AudUsuAdm", nullable = false, length = 50)
  private String adminUser;

  @Column(name = "AudIpCli", nullable = false, length = 15)
  private String clientIp;

  @Column(name = "AudObs", length = 255)
  private String observation;

  @Column(name = "AudEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
