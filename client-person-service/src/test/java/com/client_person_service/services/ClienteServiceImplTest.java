package com.client_person_service.services;

import com.client_person_service.dtos.ClienteDTO;
import com.client_person_service.entities.Cliente;
import com.client_person_service.exceptions.ClientAlreadyExistsException;
import com.client_person_service.exceptions.ClientNotFoundException;
import com.client_person_service.exceptions.ClienteAlreadyDeletedException;
import com.client_person_service.repositories.IClienteRepository;
import com.client_person_service.util.IdGenerator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceImplTest {

  @Mock
  private IClienteRepository clienteRepository;

  @Mock
  private IdGenerator idGenerator;

  @InjectMocks
  private ClienteServiceImpl clienteService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  String identificacion="1785963247";

  @Test
  void testCreateCliente_Success() {
    ClienteDTO clienteDTO = new ClienteDTO();
    clienteDTO.setClienteId(null); // Para probar la generación de ID
    clienteDTO.setIdentificacion(identificacion);
    clienteDTO.setNombre("Juan Perez");
    clienteDTO.setContrasena("laDesiempre");
    clienteDTO.setEstado(true);

    Cliente cliente = new Cliente();
    cliente.setClienteId(1L);
    cliente.setIdentificacion(identificacion);

    when(clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())).thenReturn(false);
    when(idGenerator.generateNextId()).thenReturn(1L);
    when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

    ClienteDTO result = clienteService.createCliente(clienteDTO);

    assertNotNull(result);
    assertEquals(clienteDTO.getIdentificacion(), result.getIdentificacion());
    verify(clienteRepository, times(1)).save(any(Cliente.class));
  }

  @Test
  void testCreateCliente_ClientAlreadyExistsException() {
    ClienteDTO clienteDTO = new ClienteDTO();
    clienteDTO.setIdentificacion("12345");

    when(clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())).thenReturn(true);

    ClientAlreadyExistsException thrown = assertThrows(ClientAlreadyExistsException.class, () -> {
      clienteService.createCliente(clienteDTO);
    });

    assertEquals("Cliente con la identificación 12345 ya existe.", thrown.getMessage());
  }

  @Test
  void testGetAllClientes() {
    Cliente cliente = new Cliente();
    cliente.setNombre("Juan Perez");

    when(clienteRepository.findAll()).thenReturn(List.of(cliente));

    List<ClienteDTO> result = clienteService.getAllClientes();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals("Juan Perez", result.get(0).getNombre());
  }

  @Test
  void testGetClienteById_Success() {
    Cliente cliente = new Cliente();
    cliente.setClienteId(1L);
    cliente.setNombre("Juan Perez");

    when(clienteRepository.findClienteIdByIdCliente(1L)).thenReturn(Optional.of(cliente));

    ClienteDTO result = clienteService.getClienteById(1L);

    assertNotNull(result);
    assertEquals("Juan Perez", result.getNombre());
  }

  @Test
  void testGetClienteById_ClientNotFoundException() {
    when(clienteRepository.findClienteIdByIdCliente(1L)).thenReturn(Optional.empty());

    ClientNotFoundException thrown = assertThrows(ClientNotFoundException.class, () -> {
      clienteService.getClienteById(1L);
    });

    assertEquals("Cliente no encontrado con ID: 1", thrown.getMessage());
  }

  @Test
  void testUpdateCliente_Success() {
    ClienteDTO clienteDTO = new ClienteDTO();
    clienteDTO.setNombre("Juan Perez Actualizado");

    Cliente cliente = new Cliente();
    cliente.setClienteId(1L);
    cliente.setNombre("Juan Perez");

    when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
    when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

    ClienteDTO result = clienteService.updateCliente(1L, clienteDTO);

    assertNotNull(result);
    assertEquals("Juan Perez Actualizado", result.getNombre());
  }

  @Test
  void testUpdateCliente_ClientNotFoundException() {
    ClienteDTO clienteDTO = new ClienteDTO();

    when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

    ClientNotFoundException thrown = assertThrows(ClientNotFoundException.class, () -> {
      clienteService.updateCliente(1L, clienteDTO);
    });

    assertEquals("Cliente no encontrado con ID: 1", thrown.getMessage());
  }

  @Test
  void testDeleteCliente_Success() {
    Cliente cliente = new Cliente();
    cliente.setClienteId(1L);
    cliente.setEstado(true);

    when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

    clienteService.deleteCliente(1L);

    verify(clienteRepository, times(1)).softDelete(1L);
  }

  @Test
  void testDeleteCliente_ClienteAlreadyDeletedException() {
    Cliente cliente = new Cliente();
    cliente.setClienteId(1L);
    cliente.setEstado(false);

    when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

    ClienteAlreadyDeletedException thrown = assertThrows(ClienteAlreadyDeletedException.class, () -> {
      clienteService.deleteCliente(1L);
    });

    assertEquals("El cliente con ID 1 ya ha sido eliminado.", thrown.getMessage());
  }

  @Test
  void testObtenerClienteIdPorIdentificacion_Success() {
    when(clienteRepository.findClienteIdByIdentificacion(identificacion)).thenReturn(1L);

    Long result = clienteService.obtenerClienteIdPorIdentificacion(identificacion);

    assertNotNull(result);
    assertEquals(1L, result);
  }

  @Test
  void testObtenerClienteIdPorIdentificacion_ClientNotFoundException() {
    when(clienteRepository.findClienteIdByIdentificacion("147852369")).thenReturn(null);

    ClientNotFoundException thrown = assertThrows(ClientNotFoundException.class, () -> {
      clienteService.obtenerClienteIdPorIdentificacion("147852369");
    });

    assertEquals("Cliente no encontrado con la identificación: 147852369", thrown.getMessage());
  }
}
