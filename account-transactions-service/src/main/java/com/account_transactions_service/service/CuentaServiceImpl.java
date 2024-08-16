package com.account_transactions_service.service;

import com.account_transactions_service.config.ApiConfig;
import com.account_transactions_service.dtos.CuentaDTO;
import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.exceptions.CuentaNoEncontradaException;
import com.account_transactions_service.repositories.ICuentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CuentaServiceImpl implements ICuentaService {


  private final RestTemplate restTemplate;
  private final ICuentaRepository cuentaRepository;
  private final ApiConfig apiConfig;

  public CuentaServiceImpl(RestTemplate restTemplate, ICuentaRepository cuentaRepository,
      ApiConfig apiConfig) {
    this.restTemplate = restTemplate;
    this.cuentaRepository = cuentaRepository;
    this.apiConfig = apiConfig;
  }

  @Override
  @Transactional
  public CuentaDTO createCuenta(String identificacion, CuentaDTO cuentaDTO) {
    try {
      //se invoca el servicio externo para obtener el iD_cliente por medio de la indentificacion
      String url = apiConfig.getApiClientCuentaUrl() + identificacion;
      Long clientId = restTemplate.getForObject(url, Long.class);

      if (clientId != null) {
        Cuenta cuenta = dtoToEntity(cuentaDTO);
        cuenta.setClienteId(clientId);
        Optional<Cuenta> cuentaExistente = cuentaRepository.findByNumeroCuenta(
            cuenta.getNumeroCuenta());
        if (cuentaExistente.isPresent()) {
          log.error("CuentaServiceImpl La cuenta con el número" + cuenta.getNumeroCuenta() + " ya existe.");
          throw new RuntimeException(
              "La cuenta con el número " + cuenta.getNumeroCuenta() + " ya existe.");
        }
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return entityToDto(cuentaGuardada);
      } else {
        log.error("CuentaServiceImpl Cliente no encontrado con la identificación: " + identificacion);
        throw new RuntimeException(
            "Cliente no encontrado con la identificación: " + identificacion);
      }
    } catch (HttpClientErrorException.NotFound e) {
      log.error("CuentaServiceImpl Cliente no encontrado en el servicio externo: ");
      throw new RuntimeException("Cliente no encontrado en el servicio externo: " + e.getMessage(),
          e);
    } catch (HttpServerErrorException e) {
      log.error("CuentaServiceImpl Error del servidor al obtener datos del cliente: " + e.getMessage(), e);
      throw new RuntimeException(
          "Error del servidor al obtener datos del cliente: " + e.getMessage(), e);
    } catch (RestClientException e) {
      log.error("CuentaServiceImpl Error al realizar la solicitud al servicio externo: ");
      throw new RuntimeException(
          "Error al realizar la solicitud al servicio externo: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<CuentaDTO> getAllCuentas() {
    try {
      List<CuentaDTO> cuentaDTOList = cuentaRepository.findAll().stream()
          .map(this::entityToDto)
          .collect(Collectors.toList());
      if (!cuentaDTOList.isEmpty()) {
        return cuentaRepository.findAll().stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());

      } else {
        log.error("CuentaServiceImpl No existen cuentas en la Base de datos");
        throw new CuentaNoEncontradaException("No existen cuentas en la Base de datos");
      }

    } catch (CuentaNoEncontradaException e) {
      throw e;
    } catch (Exception e) {
      log.error("CuentaServiceImpl Error al consultar las cuentas",e.getMessage(),e);
      throw new RuntimeException("Error al consultar las cuentas");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CuentaDTO> getCuentaById(String id) {
    try {
      Optional<CuentaDTO> cuentaDTOptional = cuentaRepository.findByNumeroCuenta(id)
          .map(this::entityToDto);
      if (cuentaDTOptional.isPresent()) {

        return cuentaRepository.findByNumeroCuenta(id).map(this::entityToDto);
      } else {
        throw new CuentaNoEncontradaException("Cuenta no encontrada con ID: " + id);
      }
    } catch (CuentaNoEncontradaException e) {
      log.error("CuentaServiceImpl Cuenta no encontrada: " + e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("CuentaServiceImpl Error al buscar la cuenta: " + e.getMessage(), e);
      throw new RuntimeException("Error al buscar la cuenta", e);
    }

  }


  @Override
  @Transactional
  public CuentaDTO updateCuenta(String id, CuentaDTO cuentaDTO) {
    try {
      Optional<Cuenta> cuentaOptional = cuentaRepository.findByNumeroCuenta(id);
      if (cuentaOptional.isPresent()) {
        Cuenta cuenta = cuentaOptional.get();

        // por seguridad NO modifica los campos numeroCuenta, saldo ni clienteId
        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setEstado(cuentaDTO.getEstado());
        cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return entityToDto(cuentaActualizada);
      } else {
        throw new CuentaNoEncontradaException("Cuenta no encontrada con ID: " + id);
      }
    } catch (CuentaNoEncontradaException e) {
      log.error("CuentaServiceImpl Cuenta no encontrada: " + e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("CuentaServiceImpl Error al actualizar la cuenta: " + e.getMessage(), e);
      throw new RuntimeException("Error al actualizar la cuenta", e);
    }
  }


  @Override
  @Transactional
  public ResponseEntity<String> deleteCuenta(String id) {
    try {
      Optional<Cuenta> cuentaOptional = cuentaRepository.findByNumeroCuenta(id);
      if (cuentaOptional.isPresent()) {
        Cuenta cuenta = cuentaOptional.get();
        cuenta.setEstado(false);
        cuentaRepository.save(cuenta);
        return ResponseEntity.ok("Cuenta con Id: " + id + " borrada con éxito");
      } else {
        throw new CuentaNoEncontradaException("Cuenta no encontrada con ID: " + id);

      }
    } catch (CuentaNoEncontradaException e) {
      log.error("Cuenta no encontrada con ID: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("Error al eliminar la cuenta: {}", e.getMessage(), e);
      throw new RuntimeException("Error al eliminar la cuenta", e);
    }
  }


  @Override
  @Transactional(readOnly = true)
  public Optional<CuentaDTO> buscarPorNumeroCuenta(String numeroCuenta) {
    try {
      Optional<CuentaDTO> cuentaDTOOptional = cuentaRepository.findByNumeroCuenta(numeroCuenta)
          .map(this::entityToDto);
      if (cuentaDTOOptional.isPresent()) {

        return cuentaDTOOptional;
      } else {
        throw new CuentaNoEncontradaException(
            "Número de cuenta : " + cuentaDTOOptional.get().getNumeroCuenta() + " no encontrado");
      }

    } catch (CuentaNoEncontradaException e) {
      log.error(" CuentaServiceImpl Número de cuenta no encontrado", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error al buscar la cuenta por numero de cuenta");
    }

  }

  @Override
  @Transactional(readOnly = true)
  public List<CuentaDTO> obtenerCuentasPorCliente(Long clienteId) {
    return cuentaRepository.findByClienteId(clienteId).stream()
        .map(this::entityToDto)
        .collect(Collectors.toList());
  }

  // Metodo de conversion DTO a Entity
  private Cuenta dtoToEntity(CuentaDTO cuentaDTO) {
    Cuenta cuenta = new Cuenta();
    cuenta.setCuentaId(cuentaDTO.getCuentaId());
    cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
    cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
    cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
    cuenta.setEstado(cuentaDTO.getEstado());
    cuenta.setClienteId(cuentaDTO.getClienteId());
    return cuenta;
  }

  // Metodo de conversion  Entity a DTO
  private CuentaDTO entityToDto(Cuenta cuenta) {
    CuentaDTO cuentaDTO = new CuentaDTO();
    cuentaDTO.setCuentaId(cuenta.getCuentaId());
    cuentaDTO.setNumeroCuenta(cuenta.getNumeroCuenta());
    cuentaDTO.setTipoCuenta(cuenta.getTipoCuenta());
    cuentaDTO.setSaldoInicial(cuenta.getSaldoInicial());
    cuentaDTO.setEstado(cuenta.getEstado());
    cuentaDTO.setClienteId(cuenta.getClienteId());
    return cuentaDTO;
  }
}
