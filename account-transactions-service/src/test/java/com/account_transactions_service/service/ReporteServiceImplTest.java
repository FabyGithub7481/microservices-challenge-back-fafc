package com.account_transactions_service.service;

import com.account_transactions_service.config.ApiConfig;
import com.account_transactions_service.dtos.ReporteEstadoCuentaDTO;
import com.account_transactions_service.dtos.ReporteEstadoCuentaDTO.CuentaReporteDTO;
import com.account_transactions_service.dtos.ReporteEstadoCuentaDTO.MovimientoReporteDTO;
import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.entities.Movimientos;
import com.account_transactions_service.models.Cliente;
import com.account_transactions_service.repositories.ICuentaRepository;
import com.account_transactions_service.repositories.IMovimientosRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReporteServiceImplTest {

  @Mock
  private ICuentaRepository cuentaRepository;

  @Mock
  private IMovimientosRepository movimientosRepository;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ApiConfig apiConfig;

  @InjectMocks
  private ReporteServiceImpl reporteService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }


  String urlTestCliente ="http://api/clientes/";

  @Test
  void testGenerateReporteEstadoCuenta_Success() {

    Long clienteId = 158963147L;
    LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
    LocalDate fechaFin = LocalDate.of(2024, 12, 31);


    Cliente cliente = new Cliente();
    cliente.setClienteId(clienteId);
    cliente.setNombre("Martin Luterking");


    Cuenta cuenta = new Cuenta();
    cuenta.setNumeroCuenta("147852369");
    cuenta.setSaldoInicial(BigDecimal.valueOf(1620.11));


    Movimientos movimiento = new Movimientos();
    movimiento.setFecha(LocalDateTime.of(2024, 6, 15, 0, 0)); // LocalDateTime
    movimiento.setTipoMovimiento("DEPOSITO");
    movimiento.setValor(new BigDecimal("250.56"));
    movimiento.setSaldo(new BigDecimal("1202.45"));
    movimiento.setCuenta(cuenta);


    when(apiConfig.getApiClientUrl()).thenReturn(urlTestCliente);
    when(restTemplate.getForObject(urlTestCliente + clienteId, Cliente.class))
        .thenReturn(cliente);
    when(cuentaRepository.findByClienteId(clienteId)).thenReturn(List.of(cuenta));
    when(movimientosRepository.findByCuentaAndFechaBetween(cuenta, fechaInicio, fechaFin))
        .thenReturn(List.of(movimiento));


    ReporteEstadoCuentaDTO reporte = reporteService.generateReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);


    assertNotNull(reporte);
    assertEquals(clienteId, reporte.getClienteId());
    assertEquals("Martin Luterking", reporte.getNombreCliente());
    assertEquals(1, reporte.getCuentas().size());

    CuentaReporteDTO cuentaReporteDTO = reporte.getCuentas().get(0);
    assertEquals("147852369", cuentaReporteDTO.getNumeroCuenta());
    assertEquals(1620.11, cuentaReporteDTO.getSaldo().doubleValue());

    assertEquals(1, cuentaReporteDTO.getMovimientos().size());
    MovimientoReporteDTO movimientoReporteDTO = cuentaReporteDTO.getMovimientos().get(0);
    assertEquals(LocalDateTime.of(2024, 6, 15, 0, 0), movimientoReporteDTO.getFecha());
    assertEquals("DEPOSITO", movimientoReporteDTO.getTipoMovimiento());
    assertEquals(new BigDecimal("250.56"), movimientoReporteDTO.getValorMovimiento());
    assertEquals(new BigDecimal("1202.45"), movimientoReporteDTO.getSaldo());
  }


  @Test
  void testGenerateReporteEstadoCuenta_ClienteNotFound() {

    Long clienteId = 1L;
    LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
    LocalDate fechaFin = LocalDate.of(2024, 12, 31);

    when(apiConfig.getApiClientUrl()).thenReturn(urlTestCliente);
    when(restTemplate.getForObject(urlTestCliente + clienteId, Cliente.class))
        .thenReturn(null);

    ReporteEstadoCuentaDTO reporte = reporteService.generateReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);

    assertNotNull(reporte);
    assertNull(reporte.getClienteId());
    assertEquals("Cliente no encontrado", reporte.getNombreCliente());
    assertTrue(reporte.getCuentas().isEmpty());
  }



  @Test
  void testGenerateReporteEstadoCuenta_HttpServerErrorException() {
    Long clienteId = 1L;
    LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
    LocalDate fechaFin = LocalDate.of(2024, 12, 31);

    when(apiConfig.getApiClientUrl()).thenReturn(urlTestCliente);
    when(restTemplate.getForObject(urlTestCliente + clienteId, Cliente.class))
        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      reporteService.generateReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);
    });

    assertEquals("Error del servidor al obtener datos del cliente: 500 INTERNAL_SERVER_ERROR", thrown.getMessage());
  }

  @Test
  void testGenerateReporteEstadoCuenta_RestClientException() {
    Long clienteId = 1L;
    LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
    LocalDate fechaFin = LocalDate.of(2024, 12, 31);

    when(apiConfig.getApiClientUrl()).thenReturn(urlTestCliente);
    when(restTemplate.getForObject(urlTestCliente + clienteId, Cliente.class))
        .thenThrow(new RestClientException("Error servicio externo"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      reporteService.generateReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);
    });

    assertEquals("Error al realizar la solicitud al servicio externo: Error servicio externo", thrown.getMessage());
  }

  @Test
  void testGenerateReporteEstadoCuenta_GenericException() {
    Long clienteId = 1L;
    LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
    LocalDate fechaFin = LocalDate.of(2024, 12, 31);

    when(apiConfig.getApiClientUrl()).thenReturn(urlTestCliente);
    when(restTemplate.getForObject(urlTestCliente + clienteId, Cliente.class))
        .thenThrow(new RuntimeException("Error interno"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      reporteService.generateReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);
    });

    assertEquals("Error interno del servidor: Error interno", thrown.getMessage());
  }
}

