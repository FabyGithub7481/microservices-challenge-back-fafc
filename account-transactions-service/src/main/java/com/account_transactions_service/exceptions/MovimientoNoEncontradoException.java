package com.account_transactions_service.exceptions;

public class MovimientoNoEncontradoException extends RuntimeException {
  public MovimientoNoEncontradoException(String message) {
    super(message);
  }
}