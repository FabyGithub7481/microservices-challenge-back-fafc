package com.client_person_service.repositories;

import com.client_person_service.entities.Cliente;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IClienteRepository extends JpaRepository<Cliente, Long> {

  @Modifying
  @Transactional
  @Query("UPDATE Cliente c SET c.estado = false WHERE c.id = :personaId")
  void softDelete(@Param("personaId") Long personaId);

  @Query("SELECT c.clienteId FROM Cliente c WHERE c.identificacion = :identificacion")
  Long findClienteIdByIdentificacion(@Param("identificacion") String identificacion);

  boolean existsByIdentificacion(String identificacion);

  @Query("SELECT c FROM Cliente c WHERE c.clienteId = :clienteId")
  Optional<Cliente> findClienteIdByIdCliente(@Param("clienteId") Long clienteId);
}
