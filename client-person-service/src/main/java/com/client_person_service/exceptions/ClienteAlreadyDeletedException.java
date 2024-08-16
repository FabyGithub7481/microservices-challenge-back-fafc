package com.client_person_service.exceptions;

public class ClienteAlreadyDeletedException extends RuntimeException{
  public ClienteAlreadyDeletedException(String message) {
    super(message);
  }
}
