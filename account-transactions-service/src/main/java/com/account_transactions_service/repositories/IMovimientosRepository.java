package com.account_transactions_service.repositories;


import com.account_transactions_service.dtos.MovimientosDTO;
import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.entities.Movimientos;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IMovimientosRepository extends JpaRepository<Movimientos, Long> {

  @Query("SELECT m FROM Movimientos m WHERE m.cuenta = :cuenta AND " +
      "FUNCTION('DATE', m.fecha) BETWEEN :fechaInicio AND :fechaFin")
  List<Movimientos> findByCuentaAndFechaBetween(
      @Param("cuenta") Cuenta cuenta,
      @Param("fechaInicio") LocalDate fechaInicio,
      @Param("fechaFin") LocalDate fechaFin);

}
