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


  private final ICuentaRepository cuentaRepository;
  private final  IMovimientosRepository movimientosRepository;
  private final  RestTemplate restTemplate;
  private final  ApiConfig apiConfig;
  public ReporteServiceImpl(ICuentaRepository cuentaRepository,IMovimientosRepository movimientosRepository,RestTemplate restTemplate,ApiConfig apiConfig){
    this.cuentaRepository = cuentaRepository;
    this.movimientosRepository=movimientosRepository;
    this.restTemplate = restTemplate;
    this.apiConfig = apiConfig;
  }

  @Override
  @Transactional(readOnly = true)
  public ReporteEstadoCuentaDTO generateReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio,
      LocalDate fechaFin) {
    ReporteEstadoCuentaDTO reporte = new ReporteEstadoCuentaDTO();
    try {
      String url = apiConfig.getApiClientUrl() + clienteId;
      //consume servicio de cliente para traer la informacion de un cliente por medio del clienteId
      Cliente cliente = restTemplate.getForObject(url, Cliente.class);
      if (cliente != null) {
        reporte.setClienteId(cliente.getClienteId());
        reporte.setNombreCliente(cliente.getNombre());

        // Obtener las cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);
        List<CuentaReporteDTO> cuentasDTO = cuentas.stream().map(cuenta -> {
          CuentaReporteDTO cuentaReporteDTO = new CuentaReporteDTO();
          cuentaReporteDTO.setNumeroCuenta(cuenta.getNumeroCuenta());
          cuentaReporteDTO.setSaldo(cuenta.getSaldoInicial());

          // Obtener los movimientos para cada cuenta
          List<Movimientos> movimientos = movimientosRepository.findByCuentaAndFechaBetween(cuenta,
              fechaInicio, fechaFin);
          List<MovimientoReporteDTO> movimientosDTO = movimientos.stream()
              .map(movimiento -> {
                MovimientoReporteDTO movimientoReporteDTO = new MovimientoReporteDTO();
                movimientoReporteDTO.setFecha(movimiento.getFecha());
                movimientoReporteDTO.setTipoMovimiento(movimiento.getTipoMovimiento());
                movimientoReporteDTO.setValorMovimiento(movimiento.getValor());
                movimientoReporteDTO.setSaldo(movimiento.getSaldo());
                return movimientoReporteDTO;
              }).collect(Collectors.toList());

          cuentaReporteDTO.setMovimientos(movimientosDTO);
          return cuentaReporteDTO;
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