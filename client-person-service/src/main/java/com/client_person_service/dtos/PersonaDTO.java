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
public class PersonaDTO {

  @NotNull(message = "El ID es obligatorio.")
  private Long id;

  @NotBlank(message = "El nombre es obligatorio.")
  @Size(max = 255, message = "El nombre no puede tener más de 255 caracteres.")
  private String nombre;

  @NotBlank(message = "El género es obligatorio.")
  @Size(max = 20, message = "El género no puede tener más de 20 caracteres.")
  private String genero;

  @NotNull(message = "La edad es obligatoria.")
  private Integer edad;

  @NotBlank(message = "La identificación es obligatoria.")
  @Size(max = 50, message = "La identificación no puede tener más de 50 caracteres.")
  private String identificacion;

  @Size(max = 255, message = "La dirección no puede tener más de 255 caracteres.")
  private String direccion;

  @Size(max = 15, message = "El teléfono no puede tener más de 15 caracteres.")
  private String telefono;
}
