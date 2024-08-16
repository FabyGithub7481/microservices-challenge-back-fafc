package com.account_transactions_service.service;

import com.account_transactions_service.dtos.CuentaDTO;
import com.account_transactions_service.dtos.MovimientosDTO;
import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.entities.Movimientos;
import com.account_transactions_service.exceptions.CuentaNoEncontradaException;
import com.account_transactions_service.exceptions.MovimientoNoEncontradoException;
import com.account_transactions_service.exceptions.ResourceNotFoundException;
import com.account_transactions_service.exceptions.SaldoInsuficienteException;
import com.account_transactions_service.repositories.ICuentaRepository;
import com.account_transactions_service.repositories.IMovimientosRepository;
import com.account_transactions_service.util.CalculoMovimientos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MovimientosServiceImpl implements IMovimientosService {


  private final IMovimientosRepository movimientosRepository;
  private final ICuentaRepository cuentaRepository;
  private final CalculoMovimientos calculoMovimientos;

  public MovimientosServiceImpl(IMovimientosRepository movimientosRepository,
      ICuentaRepository cuentaRepository, CalculoMovimientos calculoMovimientos) {
    this.movimientosRepository = movimientosRepository;
    this.cuentaRepository = cuentaRepository;
    this.calculoMovimientos = calculoMovimientos;
  }

  @Override
  @Transactional(readOnly = true)
  public List<MovimientosDTO> findAll() {
    List<Movimientos> movimientosList = movimientosRepository.findAll();
    return movimientosList.stream()
        .map(this::convertToDTO)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<MovimientosDTO> findById(Long id) {
    return movimientosRepository.findById(id)
        .map(this::convertToDTO);
  }

  @Override
  @Transactional
  public MovimientosDTO save(MovimientosDTO movimientosDTO) {
    Optional<Cuenta> cuentaOptional = cuentaRepository.findByNumeroCuenta(
        movimientosDTO.getCuenta().getNumeroCuenta());

    if (!cuentaOptional.isPresent()) {
      log.error("La cuenta con número "
          + movimientosDTO.getCuenta().getNumeroCuenta() + " no existe");
      throw new CuentaNoEncontradaException("La cuenta con número "
          + movimientosDTO.getCuenta().getNumeroCuenta() + " no existe");
    }
    if (!cuentaOptional.get().getEstado()) {
      throw new CuentaNoEncontradaException(
          "No es posible realizar movimientos en la cuenta con número "
              + movimientosDTO.getCuenta().getNumeroCuenta()
              + " actualmente se encuentra deshabilita comuniquese con la entidad bancaria");
    }

    Cuenta cuenta = cuentaOptional.get();

    try {
      Movimientos movimientos = convertToEntity(movimientosDTO);
      calculoMovimientos.actualizarSaldo(cuenta, movimientos.getValor(),
          movimientos.getTipoMovimiento());

      movimientos.setSaldo(cuenta.getSaldoInicial());
      movimientos.setCuenta(cuenta);

      cuentaRepository.save(cuenta);
      movimientosRepository.save(movimientos);

      // Convierte la entidad guardada a DTO y retorna
      return convertToDTO(movimientos);

    } catch (SaldoInsuficienteException e) {
      log.error("MovimientosServiceImpl cuenta con saldo insuficiente para la transaccion: "
          + e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error al guardar el movimiento", e);
    }
  }

  @Override
  @Transactional
  public MovimientosDTO update(Long id, MovimientosDTO movimientosDTO) {
    try {
      if (movimientosDTO == null) {
        throw new IllegalArgumentException("El DTO de movimiento no puede ser nulo");
      }

      if (movimientosDTO.getCuenta() == null) {
        throw new IllegalArgumentException("El DTO de cuenta no puede ser nulo");
      }

      Movimientos movimientoExistente = movimientosRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));

      // por no descuadrar la contabilidad NO modifica varios campos
      movimientoExistente.setFecha(movimientosDTO.getFecha());
      movimientoExistente.setTipoMovimiento(movimientosDTO.getTipoMovimiento());
      movimientoExistente.setValor(movimientosDTO.getValor());

      if (movimientosDTO.getCuenta() != null) {
        Cuenta cuenta = cuentaRepository.findById(movimientosDTO.getCuenta().getCuentaId())
            .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        movimientoExistente.setCuenta(cuenta);
      }

      // Guarda el movimiento actualizado
      Movimientos movimientoActualizado = movimientosRepository.save(movimientoExistente);
      return convertToDTO(movimientoActualizado);

    } catch (MovimientoNoEncontradoException e) {
      throw e;
    } catch (Exception e) {
      log.error("MovimientosServiceImpl Error al actualizar el movimiento", e.getMessage(), e);
      throw new RuntimeException("Error al actualizar el movimiento", e);
    }
  }


  @Override
  @Transactional
  public ResponseEntity<String> deleteById(Long id) {
    try {
      Optional<Movimientos> movimientosOptional = movimientosRepository.findById(id);
      if (movimientosOptional.isPresent()) {
        movimientosRepository.deleteById(id);
        return ResponseEntity.ok("Movimiento con ID: " + id + " eliminado exitosamente");
      } else {
        log.error("MovimientosServiceImpl Movimiento no encontrado con ID: " + id);
        throw new MovimientoNoEncontradoException("Movimiento con ID: " + id + " no encontrado");
      }
    } catch (MovimientoNoEncontradoException e) {
      throw e;
    } catch (Exception e) {
      log.error("MovimientosServiceImpl Error al eliminar el movimiento", e.getMessage(), e);
      throw new RuntimeException("Error al eliminar el movimiento", e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<MovimientosDTO> obtenerMovimientosPorCuentaYFecha(Long cuentaId,
      LocalDate fechaInicio, LocalDate fechaFin) {
    try {
      Optional<Cuenta> cuentaOptional = cuentaRepository.findById(cuentaId);
      if (cuentaOptional.isPresent()) {
        Cuenta cuenta = cuentaOptional.get();
        List<Movimientos> movimientosList = movimientosRepository.findByCuentaAndFechaBetween(
            cuenta,
            fechaInicio, fechaFin);
        return movimientosList.stream()
            .map(this::convertToDTO)
            .toList();
      } else {

        return List.of(); // Retorna una lista vacía si la cuenta no existe
      }

    } catch (CuentaNoEncontradaException e) {
      throw e;
    } catch (Exception e) {
      log.error("ReporteerviceImpl Error obtener Movimientos Por Cuenta Y Fecha", e.getMessage(),
          e);
      throw new RuntimeException("Error obtener Movimientos Por Cuenta y Fecha", e);
    }
  }


  private MovimientosDTO convertToDTO(Movimientos movimientos) {
    if (movimientos == null) {
      return null;
    }

    Cuenta cuenta = movimientos.getCuenta();

    return MovimientosDTO.builder()
        .id(movimientos.getId())
        .fecha(movimientos.getFecha())
        .tipoMovimiento(movimientos.getTipoMovimiento())
        .valor(movimientos.getValor())
        .saldo(movimientos.getSaldo())
        .cuenta(cuenta != null ? CuentaDTO.builder() // si cuenta viene null ya no se cae
            .cuentaId(cuenta.getCuentaId())
            .numeroCuenta(cuenta.getNumeroCuenta())
            .tipoCuenta(cuenta.getTipoCuenta())
            .saldoInicial(cuenta.getSaldoInicial())
            .estado(cuenta.getEstado())
            .clienteId(cuenta.getClienteId())
            .build() : null)
        .build();
  }

  private Movimientos convertToEntity(MovimientosDTO movimientosDTO) {
    Cuenta cuenta = cuentaRepository.findByNumeroCuenta(
            movimientosDTO.getCuenta().getNumeroCuenta())
        .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
    return Movimientos.builder()
        .id(movimientosDTO.getId())
        .fecha(movimientosDTO.getFecha())
        .tipoMovimiento(movimientosDTO.getTipoMovimiento())
        .valor(movimientosDTO.getValor())
        .saldo(movimientosDTO.getSaldo())
        .cuenta(cuenta)
        .build();
  }
}
