package com.tetoca.tetoca_api.tenant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "A1M_AGENCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Agency {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "AgeCod")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AgeDivCod", nullable = false)
  @ToString.Exclude
  private Division division;

  @Column(name = "AgeNom", nullable = false, length = 100)
  private String name;

  @Column(name = "AgeDir", length = 150)
  private String address;

  @Column(name = "AgeRef", length = 100)
  private String reference;

  @Column(name = "AgeEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
