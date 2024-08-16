package com.account_transactions_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaDTO {

  @NotNull(message = "El ID de la cuenta no puede ser nulo")
  private Long cuentaId;

  @NotBlank(message = "El numero de cuenta no puede estar vacío")
  @Size(max = 20, message = "El número de cuenta no puede exceder los 20 caracteres")
  private String numeroCuenta;

  @NotBlank(message = "El tipo de cuenta no puede estar vacío")
  @Size(max = 50, message = "El tipo de cuenta no puede exceder los 50 caracteres")
  private String tipoCuenta;

  @NotNull(message = "El saldo inicial no puede ser nulo")
  @Positive(message = "El saldo inicial debe ser un valor positivo")
  private BigDecimal saldoInicial;

  @NotNull(message = "El estado de la cuenta no puede ser nulo")
  private Boolean estado;

  @NotNull(message = "El ID del cliente no puede ser nulo")
  private Long clienteId;
}
