package com.client_person_service.services;

import com.client_person_service.dtos.ClienteDTO;
import com.client_person_service.exceptions.ClientNotFoundException;
import com.client_person_service.exceptions.ClientAlreadyExistsException;

import java.util.List;

public interface IClienteService {

  ClienteDTO createCliente(ClienteDTO clienteDTO) throws ClientAlreadyExistsException;

  List<ClienteDTO> getAllClientes();

  ClienteDTO getClienteById(Long id) throws ClientNotFoundException;

  ClienteDTO updateCliente(Long id, ClienteDTO clienteDTO) throws ClientNotFoundException;

  void deleteCliente(Long id) throws ClientNotFoundException;

  Long obtenerClienteIdPorIdentificacion(String identificacion) throws ClientNotFoundException;
}
