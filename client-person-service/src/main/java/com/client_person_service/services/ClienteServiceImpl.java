package com.client_person_service.services;

import com.client_person_service.dtos.ClienteDTO;
import com.client_person_service.entities.Cliente;
import com.client_person_service.exceptions.ClientNotFoundException;
import com.client_person_service.exceptions.ClientAlreadyExistsException;
import com.client_person_service.exceptions.ClienteAlreadyDeletedException;
import com.client_person_service.exceptions.DataIntegrityViolationException;
import com.client_person_service.repositories.IClienteRepository;
import com.client_person_service.util.IdGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClienteServiceImpl implements IClienteService {

  private final IClienteRepository clienteRepository;
  private final IdGenerator idGenerator;

  public ClienteServiceImpl(IClienteRepository clienteRepository, IdGenerator idGenerator) {
    this.clienteRepository = clienteRepository;
    this.idGenerator = idGenerator;
  }

  @Override
  @Transactional
  public ClienteDTO createCliente(ClienteDTO clienteDTO) {
  try{
    Long clienteId = clienteDTO.getClienteId();
    if (clienteId == null) {
      clienteId = idGenerator.generateNextId();
    }

    // Verifica si el cliente ya existe por identificación
    if (clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
      throw new ClientAlreadyExistsException(
          "Cliente con la identificación " + clienteDTO.getIdentificacion() + " ya existe.");
    }

    // Mapea el DTO a la entidad
    Cliente cliente = new Cliente();
    cliente.setClienteId(clienteId);
    cliente.setContrasena(clienteDTO.getContrasena());
    cliente.setEstado(clienteDTO.getEstado());
    cliente.setNombre(clienteDTO.getNombre());
    cliente.setGenero(clienteDTO.getGenero());
    cliente.setEdad(clienteDTO.getEdad());
    cliente.setIdentificacion(clienteDTO.getIdentificacion());
    cliente.setDireccion(clienteDTO.getDireccion());
    cliente.setTelefono(clienteDTO.getTelefono());


    Cliente savedCliente = clienteRepository.save(cliente);
    log.info("Creando nuevo cliente con ID: {}", cliente.getClienteId());

    // Mapea la entidad de vuelta al DTO
    return mapToDTO(savedCliente);

  } catch (DataIntegrityViolationException ex) {
    log.error("ClienteServiceImpl error creando nuevo cliente violacion de integridad de datos: {}", ex.getMessage(), ex);
    throw ex;
  } catch (DataAccessException ex) {
    log.error("ClienteServiceImpl error creando un nuevo cliente: {}", ex.getMessage(), ex);
    throw ex;
  }

  }

  @Override
  @Transactional(readOnly = true)
  public List<ClienteDTO> getAllClientes() {
    return clienteRepository.findAll().stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public ClienteDTO getClienteById(Long id) {
    Cliente cliente = clienteRepository.findClienteIdByIdCliente(id)
        .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID: " + id));
    return mapToDTO(cliente);
  }

  @Override
  @Transactional
  public ClienteDTO updateCliente(Long id, ClienteDTO clienteDTO) {
    Cliente cliente = clienteRepository.findById(id)
        .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID: " + id));

    cliente.setContrasena(clienteDTO.getContrasena());
    cliente.setEstado(clienteDTO.getEstado());
    cliente.setNombre(clienteDTO.getNombre());
    cliente.setGenero(clienteDTO.getGenero());
    cliente.setEdad(clienteDTO.getEdad());
    cliente.setIdentificacion(clienteDTO.getIdentificacion());
    cliente.setDireccion(clienteDTO.getDireccion());
    cliente.setTelefono(clienteDTO.getTelefono());

    Cliente updatedCliente = clienteRepository.save(cliente);
    return mapToDTO(updatedCliente);
  }

  @Override
  @Transactional
  public void deleteCliente(Long id) {
    Cliente cliente = clienteRepository.findById(id)
        .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID: " + id));

    if (!cliente.getEstado()) {
      throw new ClienteAlreadyDeletedException(
          "El cliente con ID " + id + " ya ha sido eliminado.");
    }

    clienteRepository.softDelete(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Long obtenerClienteIdPorIdentificacion(String identificacion) {
    if (identificacion == null || identificacion.isEmpty()) {
      throw new IllegalArgumentException("La identificación no puede ser nula o vacía.");
    }

    Long clienteId = clienteRepository.findClienteIdByIdentificacion(identificacion);

    if (clienteId == null) {
      throw new ClientNotFoundException(
          "Cliente no encontrado con la identificación: " + identificacion);
    }

    return clienteId;
  }

  private ClienteDTO mapToDTO(Cliente cliente) {
    return new ClienteDTO(
        cliente.getClienteId(),
        cliente.getId(),
        cliente.getNombre(),
        cliente.getGenero(),
        cliente.getEdad(),
        cliente.getIdentificacion(),
        cliente.getDireccion(),
        cliente.getTelefono(),
        cliente.getContrasena(),
        cliente.getEstado()
    );
  }
}
