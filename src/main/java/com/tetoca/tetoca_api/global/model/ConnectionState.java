package com.tetoca.tetoca_api.global.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GZZ_ESTADO_CONEXION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionState {

  @Id
  @Column(name = "EstConCod")
  private Integer id;

  @Column(name = "EstConNom", nullable = false, length = 30)
  private String name;

  @Column(name = "EstConDes", length = 100)
  private String description;

  @Column(name = "EstConEstReg", nullable = false, length = 1)
  private String recordStatus = "A";
}
