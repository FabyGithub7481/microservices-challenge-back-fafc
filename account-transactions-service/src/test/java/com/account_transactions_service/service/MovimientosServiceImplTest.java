package com.account_transactions_service.service;

import com.account_transactions_service.dtos.CuentaDTO;
import com.account_transactions_service.dtos.MovimientosDTO;
import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.entities.Movimientos;
import com.account_transactions_service.exceptions.CuentaNoEncontradaException;
import com.account_transactions_service.exceptions.MovimientoNoEncontradoException;
import com.account_transactions_service.exceptions.SaldoInsuficienteException;
import com.account_transactions_service.repositories.ICuentaRepository;
import com.account_transactions_service.repositories.IMovimientosRepository;
import com.account_transactions_service.util.CalculoMovimientos;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovimientosServiceImplTest {

  @Mock
  private IMovimientosRepository movimientosRepository;

  @Mock
  private ICuentaRepository cuentaRepository;

  @Mock
  private CalculoMovimientos calculoMovimientos;

  @InjectMocks
  private MovimientosServiceImpl movimientosService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testObtenerMovimientosPorCuentaYFecha_Success() {
    LocalDate fechaInicio = LocalDate.of(2024, 8, 1);
    LocalDate fechaFin = LocalDate.of(2024, 8, 31);
    Cuenta cuenta = Cuenta.builder().numeroCuenta("123456").build();

    List<Movimientos> movimientosList = List.of(
        Movimientos.builder().fecha(LocalDateTime.of(2024, 8, 15, 10, 30)).build()
    );

    when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
    when(movimientosRepository.findByCuentaAndFechaBetween(cuenta, fechaInicio, fechaFin)).thenReturn(movimientosList);

    List<MovimientosDTO> result = movimientosService.obtenerMovimientosPorCuentaYFecha(1L, fechaInicio, fechaFin);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }


  @Test
  void testDeleteById_MovimientoNoEncontrado() {
    when(movimientosRepository.findById(1L)).thenReturn(Optional.empty());

    MovimientoNoEncontradoException thrown = assertThrows(MovimientoNoEncontradoException.class, () -> {
      movimientosService.deleteById(1L);
    });
    assertEquals("Movimiento con ID: 1 no encontrado", thrown.getMessage());
  }

  @Test
  void testDeleteById_Success() {
    when(movimientosRepository.findById(1L)).thenReturn(Optional.of(new Movimientos()));

    ResponseEntity<String> response = movimientosService.deleteById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Movimiento con ID: 1 eliminado exitosamente", response.getBody());
  }




}
