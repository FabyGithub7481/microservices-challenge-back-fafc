package com.client_person_service.util;


import com.client_person_service.repositories.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

  private final IClienteRepository clienteRepository;
  public IdGenerator(IClienteRepository clienteRepository) {
    this.clienteRepository = clienteRepository;
  }

  public synchronized Long generateNextId() {
    Long newId;
    do {
      newId = System.currentTimeMillis();
    } while (clienteRepository.existsById(newId));
    return newId;
  }
}
