package com.account_transactions_service.service;

import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.entities.Movimientos;
import com.account_transactions_service.repository.ICuentaRepository;
import com.account_transactions_service.repository.IMovimientosRepository;
import com.account_transactions_service.response.MovimientosResponseRest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class MovimientosServiceImpl implements IMovimientosService {

  @Autowired
  private IMovimientosRepository movimientosRepository;

  @Autowired
  private ICuentaRepository cuentaRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Movimientos> findAll() {
    return movimientosRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Movimientos> findById(Long id) {
    return movimientosRepository.findById(id);
  }

  @Override
  @Transactional
  public ResponseEntity<MovimientosResponseRest> save(Movimientos movimientos) {
    MovimientosResponseRest response = new MovimientosResponseRest();
    Optional<Cuenta> cuentaOptional = cuentaRepository.findByNumeroCuenta(
        movimientos.getCuenta().getNumeroCuenta());
    try {
      if (cuentaOptional.isPresent()) {
        Cuenta cuenta = cuentaOptional.get();
        actualizarSaldo(cuenta, movimientos.getValor(), movimientos.getTipoMovimiento());
        movimientos.setSaldo(cuenta.getSaldoInicial());
        movimientos.setCuenta(cuenta);
        cuentaRepository.save(cuenta);
        movimientosRepository.save(movimientos);
        response.getMovimientosResponse().setMovimientos(movimientos);
        response.setMetadata("Respuesta OK", "00",
            movimientos.getTipoMovimiento() + " realizado!!");
      } else {
        response.setMetadata("Respuesta Fallida!!!!", "-1",
            "la cuenta " + movimientos.getCuenta().getNumeroCuenta() + " no existe");

      }
    } catch (Exception e) {
      response.setMetadata("Respuesta Fallida!!!!", "-1",
          "Error al crear el movimiento - Saldo Insuficiente en la cuenta "
              + movimientos.getCuenta().getNumeroCuenta());
      e.getStackTrace();
      return new ResponseEntity<MovimientosResponseRest>(response,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<MovimientosResponseRest>(response, HttpStatus.OK);
  }

  @Override
  @Transactional
  public void deleteById(Long id) {
    movimientosRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Movimientos> obtenerMovimientosPorCuentaYFecha(Cuenta cuenta, LocalDate fechaInicio,
      LocalDate fechaFin) {
    return movimientosRepository.findByCuentaAndFechaBetween(cuenta, fechaInicio, fechaFin);
  }


  private ResponseEntity<MovimientosResponseRest> actualizarSaldo(Cuenta cuenta, BigDecimal valor,
      String tipoMovimiento) {
    MovimientosResponseRest response = new MovimientosResponseRest();
    BigDecimal ajuste = tipoMovimiento.equalsIgnoreCase("DEPOSITO") ? valor : valor.negate();
    BigDecimal nuevoSaldo = cuenta.getSaldoInicial().add(ajuste);
    if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
      throw new RuntimeException("La cuenta " + cuenta.getNumeroCuenta()
          + " no tiene el suficiente saldo para esta transacción.");

    } else {
      cuenta.setSaldoInicial(nuevoSaldo);
      return new ResponseEntity<MovimientosResponseRest>(response, HttpStatus.OK);
    }

  }

}
