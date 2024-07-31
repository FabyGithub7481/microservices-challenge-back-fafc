package com.account_transactions_service.service;

import com.account_transactions_service.dto.ReporteEstadoCuentaDTO;
import java.time.LocalDate;

public interface IReporteService {
  ReporteEstadoCuentaDTO generateReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
