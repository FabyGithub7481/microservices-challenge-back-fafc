package com.account_transactions_service.repositories;


import com.account_transactions_service.entities.Cuenta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ICuentaRepository extends JpaRepository<Cuenta, Long> {

  @Query("SELECT c FROM Cuenta c WHERE c.numeroCuenta = :numeroCuenta")
  Optional<Cuenta> findByNumeroCuenta(@Param("numeroCuenta") String numeroCuenta);

  List<Cuenta> findByClienteId(Long clienteId);
}
