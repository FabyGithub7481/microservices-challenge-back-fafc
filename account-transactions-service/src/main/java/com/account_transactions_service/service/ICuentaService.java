package com.account_transactions_service.service;

import com.account_transactions_service.dtos.CuentaDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

public interface ICuentaService {
  CuentaDTO createCuenta(String identificacion, CuentaDTO cuentaDTO);
  List<CuentaDTO> getAllCuentas();
  Optional<CuentaDTO> getCuentaById(String id);
  CuentaDTO updateCuenta(String id, CuentaDTO cuentaDTO);
  ResponseEntity<String> deleteCuenta(String id);
  Optional<CuentaDTO> buscarPorNumeroCuenta(String numeroCuenta);
  List<CuentaDTO> obtenerCuentasPorCliente(Long clienteId);

}
