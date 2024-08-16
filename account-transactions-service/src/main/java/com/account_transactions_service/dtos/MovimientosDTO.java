package com.account_transactions_service.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientosDTO {

  private Long id;

  private LocalDateTime fecha=LocalDateTime.now();

  @NotNull(message = "El tipo de movimiento no puede ser nulo")
  @Size(max = 20, message = "El tipo de movimiento no ser mayor a 20 caracteres")
  private String tipoMovimiento;

  @NotNull(message = "El valor no puede ser nulo")
  @Positive(message = "El valor debe ser positivo")
  private BigDecimal valor;

  @NotNull(message = "El saldo no puede ser nulo")
  private BigDecimal saldo;

  @NotNull(message = "La cuenta no puede ser nula")
  private CuentaDTO cuenta;
}
