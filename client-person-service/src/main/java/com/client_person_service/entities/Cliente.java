package com.client_person_service.entities;

import com.client_person_service.util.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente extends Persona {

  @Column(nullable = false, unique = true)
  private Long clienteId;

  @Column(nullable = false)
  private String contrasena;

  @Column(nullable = false)
  private Boolean estado;


}