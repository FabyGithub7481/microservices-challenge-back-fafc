package com.account_transactions_service.service;

import com.account_transactions_service.configuracion.ApiConfig;
import com.account_transactions_service.dto.ReporteEstadoCuentaDTO;
import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.entities.Movimientos;
import com.account_transactions_service.modelo.Cliente;
import com.account_transactions_service.repository.ICuentaRepository;
import com.account_transactions_service.repository.IMovimientosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements IReporteService{

  @Autowired
  private ICuentaRepository cuentaRepository;

  @Autowired
  private IMovimientosRepository movimientosRepository;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ApiConfig apiConfig;

  @Override
  @Transactional(readOnly = true)
  public ReporteEstadoCuentaDTO generateReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio,
      LocalDate fechaFin) {
    ReporteEstadoCuentaDTO reporte = new ReporteEstadoCuentaDTO();
    try {
      String url = apiConfig.getApiClientUrl() + clienteId;
      Cliente cliente = restTemplate.getForObject(url, Cliente.class);
      if (cliente != null) {
        reporte.setClienteId(cliente.getClienteId());
        reporte.setNombreCliente(cliente.getNombre());

        // Obtener las cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);
        List<ReporteEstadoCuentaDTO.CuentaDTO> cuentasDTO = cuentas.stream().map(cuenta -> {
          ReporteEstadoCuentaDTO.CuentaDTO cuentaDTO = new ReporteEstadoCuentaDTO.CuentaDTO();
          cuentaDTO.setNumeroCuenta(cuenta.getNumeroCuenta());
          cuentaDTO.setSaldo(cuenta.getSaldoInicial());

          // Obtener los movimientos para cada cuenta
          List<Movimientos> movimientos = movimientosRepository.findByCuentaAndFechaBetween(cuenta,
              fechaInicio, fechaFin);
          List<ReporteEstadoCuentaDTO.MovimientoDTO> movimientosDTO = movimientos.stream()
              .map(movimiento -> {
                ReporteEstadoCuentaDTO.MovimientoDTO movimientoDTO = new ReporteEstadoCuentaDTO.MovimientoDTO();
                movimientoDTO.setFecha(movimiento.getFecha());
                movimientoDTO.setTipoMovimiento(movimiento.getTipoMovimiento());
                movimientoDTO.setValorMovimiento(movimiento.getValor());
                movimientoDTO.setSaldo(movimiento.getSaldo());
                return movimientoDTO;
              }).collect(Collectors.toList());

          cuentaDTO.setMovimientos(movimientosDTO);
          return cuentaDTO;
        }).collect(Collectors.toList());

        reporte.setCuentas(cuentasDTO);
      } else {

        reporte.setClienteId(null);
        reporte.setNombreCliente("Cliente no encontrado");
        reporte.setCuentas(List.of());
      }
    } catch (HttpClientErrorException.NotFound e) {

      reporte.setClienteId(null);
      reporte.setNombreCliente("Cliente no encontrado en el servicio externo");
      reporte.setCuentas(List.of());
    } catch (HttpServerErrorException e) {
      // Manejo errores del servidor en el servicio externo
      throw new RuntimeException(
          "Error del servidor al obtener datos del cliente: " + e.getMessage(), e);
    } catch (RestClientException e) {
      // Manejo errores de RestTemplate
      throw new RuntimeException(
          "Error al realizar la solicitud al servicio externo: " + e.getMessage(), e);
    } catch (Exception e) {
      // Manejo excepciones generales
      throw new RuntimeException("Error interno del servidor: " + e.getMessage(), e);
    }

    return reporte;
  }
}