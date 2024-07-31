package com.account_transactions_service.service;


import com.account_transactions_service.configuracion.ApiConfig;
import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.repository.ICuentaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CuentaServiceImpl implements ICuentaService {


  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ICuentaRepository cuentaRepository;

  @Autowired
  private ApiConfig apiConfig;

  @Override
  @Transactional
  public Cuenta createCuenta(String identificacion, Cuenta cuenta) {
    try {
      String url = apiConfig.getApiClientCuentaUrl() + identificacion;
      Long clientId = restTemplate.getForObject(url, Long.class);

      if (clientId != null) {
        cuenta.setClienteId(clientId);
        Optional<Cuenta> cuentaExistente = cuentaRepository.findByNumeroCuenta(
            cuenta.getNumeroCuenta());
        if (cuentaExistente.isPresent()) {
          throw new RuntimeException(
              "La cuenta con el número " + cuenta.getNumeroCuenta() + " ya existe.");
        }
        return cuentaRepository.save(cuenta);
      } else {
        throw new RuntimeException(
            "Cliente no encontrado con la identificación: " + identificacion);
      }
    } catch (HttpClientErrorException.NotFound e) {
      throw new RuntimeException("Cliente no encontrado en el servicio externo: " + e.getMessage(),
          e);
    } catch (HttpServerErrorException e) {

      throw new RuntimeException(
          "Error del servidor al obtener datos del cliente: " + e.getMessage(), e);
    } catch (RestClientException e) {

      throw new RuntimeException(
          "Error al realizar la solicitud al servicio externo: " + e.getMessage(), e);
    } catch (Exception e) {
      // Manejo para otras excepciones generales
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cuenta> getAllCuentas() {
    return cuentaRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Cuenta> getCuentaById(Long id) {
    return cuentaRepository.findById(id);
  }

  @Override
  @Transactional
  public Cuenta updateCuenta(Long id, Cuenta cuenta) {
    if (cuentaRepository.existsById(id)) {
      cuenta.setCuentaId(id);
      return cuentaRepository.save(cuenta);
    }
    return null;
  }

  @Override
  @Transactional
  public void deleteCuenta(Long id) {
    Optional<Cuenta> cuentaOptional = cuentaRepository.findById(id);
    if (cuentaOptional.isPresent()) {
      Cuenta cuenta = cuentaOptional.get();
      cuenta.setEstado(false);
      cuentaRepository.save(cuenta);
    } else {
      throw new RuntimeException("Cuenta no encontrada con ID: " + id);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
    return cuentaRepository.findByNumeroCuenta(numeroCuenta);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cuenta> obtenerCuentasPorCliente(Long clienteId) {
    return cuentaRepository.findByClienteId(clienteId);
  }
}

