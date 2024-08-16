package com.account_transactions_service.util;

import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.exceptions.SaldoInsuficienteException;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CalculoMovimientos {
  public void actualizarSaldo(Cuenta cuenta, BigDecimal valor, String tipoMovimiento) {
    BigDecimal ajuste = tipoMovimiento.equalsIgnoreCase("DEPOSITO") ? valor : valor.negate();
    BigDecimal nuevoSaldo = cuenta.getSaldoInicial().add(ajuste);

    if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
      throw new SaldoInsuficienteException("La cuenta " + cuenta.getNumeroCuenta()
          + " no tiene el suficiente saldo para esta transacción.");
    } else {
      cuenta.setSaldoInicial(nuevoSaldo);
    }
  }
}
