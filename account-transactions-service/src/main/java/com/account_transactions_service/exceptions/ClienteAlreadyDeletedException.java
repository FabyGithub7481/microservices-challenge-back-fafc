package com.account_transactions_service.exceptions;

public class ClienteAlreadyDeletedException extends RuntimeException{
  public ClienteAlreadyDeletedException(String message) {
    super(message);
  }
}
