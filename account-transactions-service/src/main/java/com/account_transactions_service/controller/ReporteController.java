package com.account_transactions_service.controller;


import com.account_transactions_service.dto.ReporteEstadoCuentaDTO;
import com.account_transactions_service.service.IReporteService;
import com.account_transactions_service.service.ReporteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

  @Autowired
  private IReporteService reporteService;

  @GetMapping
  public ResponseEntity<ReporteEstadoCuentaDTO> generateReporteEstadoCuenta(
      @RequestParam Long clienteId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

    ReporteEstadoCuentaDTO reporte = reporteService.generateReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);
    return ResponseEntity.ok(reporte);
  }
}

