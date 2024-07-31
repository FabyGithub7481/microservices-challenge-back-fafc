package com.account_transactions_service.response;

import com.account_transactions_service.entities.Movimientos;
import java.util.List;
import lombok.Data;

@Data
public class MovimientosResponse {

  private Movimientos movimientos;
}
