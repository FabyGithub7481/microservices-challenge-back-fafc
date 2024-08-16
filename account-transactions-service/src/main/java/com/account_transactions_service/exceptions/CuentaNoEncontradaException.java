package com.account_transactions_service.exceptions;

public class CuentaNoEncontradaException extends RuntimeException {
  public CuentaNoEncontradaException(String message) {
    super(message);
  }
}