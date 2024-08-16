package com.client_person_service.exceptions;

public class DataIntegrityViolationException extends RuntimeException{
  public DataIntegrityViolationException(String message) {
    super(message);
  }
}
