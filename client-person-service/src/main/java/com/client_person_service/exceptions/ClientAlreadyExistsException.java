package com.client_person_service.exceptions;

public class ClientAlreadyExistsException extends RuntimeException {
  public ClientAlreadyExistsException(String message) {
    super(message);
  }
}