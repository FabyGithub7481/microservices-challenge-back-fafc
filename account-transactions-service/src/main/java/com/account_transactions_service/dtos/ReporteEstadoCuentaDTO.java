package com.account_transactions_service.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteEstadoCuentaDTO {

  private Long clienteId;
  private String nombreCliente;
  private List<CuentaReporteDTO> cuentas;


  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CuentaReporteDTO {

    private String numeroCuenta;
    private BigDecimal saldo;
    private List<MovimientoReporteDTO> movimientos;


  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MovimientoReporteDTO {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private BigDecimal valorMovimiento;
    private BigDecimal saldo;


  }
}

