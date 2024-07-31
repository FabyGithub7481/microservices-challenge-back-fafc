package com.account_transactions_service.service;

import com.account_transactions_service.entities.Cuenta;
import java.util.List;
import java.util.Optional;

public interface ICuentaService {
  Cuenta createCuenta(String identificacion, Cuenta cuenta);
  List<Cuenta> getAllCuentas();
  Optional<Cuenta> getCuentaById(Long id);
  Cuenta updateCuenta(Long id, Cuenta cuenta);
  void deleteCuenta(Long id);
  Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta);
  List<Cuenta> obtenerCuentasPorCliente(Long clienteId);
}
