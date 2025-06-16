package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "G3H_EVENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {

  @Id
  @Column(name = "EveCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EveTipEveCod", nullable = false)
  private EventTypeEntity eventType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EveEmpCod", nullable = false)
  private CompanyEntity company;

  @Column(name = "EveFecHor", nullable = false)
  private Long dateTime;

  @Column(name = "EveDes", length = 255)
  private String description;

  @Column(name = "EveUsuAdm", length = 50)
  private String adminUser;

  @Builder.Default
  @Column(name = "EveEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
