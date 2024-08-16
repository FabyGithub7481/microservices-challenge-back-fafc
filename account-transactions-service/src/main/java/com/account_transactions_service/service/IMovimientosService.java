package com.account_transactions_service.service;

import com.account_transactions_service.dtos.MovimientosDTO;
import com.account_transactions_service.entities.Movimientos;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

public interface IMovimientosService {

  List<MovimientosDTO> findAll();

  Optional<MovimientosDTO> findById(Long id);

  MovimientosDTO save(MovimientosDTO movimientos);

  MovimientosDTO update(Long id, MovimientosDTO movimientosDTO);

  ResponseEntity<String> deleteById(Long id);

  List<MovimientosDTO> obtenerMovimientosPorCuentaYFecha(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin);
}
