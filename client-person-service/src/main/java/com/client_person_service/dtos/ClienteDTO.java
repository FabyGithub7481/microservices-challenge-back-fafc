package com.client_person_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO extends PersonaDTO {

  @NotNull(message = "El campo clienteId no puede ser nulo.")
  private Long clienteId;

  @NotBlank(message = "La contraseña es obligatoria.")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
  private String contrasena;

  @NotNull(message = "El estado es obligatorio.")
  private Boolean estado;

  public ClienteDTO(Long clienteId, Long id, String nombre, String genero, Integer edad,
      String identificacion, String direccion, String telefono,
      String contrasena, Boolean estado) {
    this.clienteId = clienteId;
    this.setId(id);
    this.setNombre(nombre);
    this.setGenero(genero);
    this.setEdad(edad);
    this.setIdentificacion(identificacion);
    this.setDireccion(direccion);
    this.setTelefono(telefono);
    this.contrasena = contrasena;
    this.estado = estado;
  }
}
