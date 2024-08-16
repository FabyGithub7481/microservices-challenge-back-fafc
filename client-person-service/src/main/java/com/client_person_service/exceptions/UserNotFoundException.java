package com.client_person_service.exceptions;

public class UserNotFoundException extends RuntimeException{

  public UserNotFoundException(String message){
    super(message);
  }

}
