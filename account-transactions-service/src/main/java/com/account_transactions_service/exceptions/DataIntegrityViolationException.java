package com.account_transactions_service.exceptions;

public class DataIntegrityViolationException extends RuntimeException{
  public DataIntegrityViolationException(String message) {
    super(message);
  }
}
