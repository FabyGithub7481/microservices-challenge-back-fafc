package com.account_transactions_service.service;

import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.entities.Movimientos;
import com.account_transactions_service.response.MovimientosResponseRest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

public interface IMovimientosService {
  List<Movimientos> findAll();
  Optional<Movimientos> findById(Long id);
  ResponseEntity<MovimientosResponseRest> save(Movimientos movimientos);
  void deleteById(Long id);
  List<Movimientos> obtenerMovimientosPorCuentaYFecha(Cuenta cuenta, LocalDate fechaInicio, LocalDate fechaFin);
}
