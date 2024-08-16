package com.client_person_service.controllers;

import com.client_person_service.dtos.ClienteDTO;
import com.client_person_service.exceptions.ClienteAlreadyDeletedException;
import com.client_person_service.services.IClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

  private final IClienteService clienteService;

  public ClienteController(IClienteService clienteService) {
    this.clienteService = clienteService;
  }

  @PostMapping
  public ResponseEntity<ClienteDTO> createCliente(@RequestBody ClienteDTO clienteDTO) {
    ClienteDTO nuevoClienteDTO = clienteService.createCliente(clienteDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(nuevoClienteDTO);

  }

  @GetMapping
  public ResponseEntity<List<ClienteDTO>> getAllClientes() {
    List<ClienteDTO> clientesDTO = clienteService.getAllClientes();
    return new ResponseEntity<>(clientesDTO, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClienteDTO> getClienteById(@PathVariable Long id) {
    ClienteDTO clienteDTO = clienteService.getClienteById(id);
    return ResponseEntity.ok(clienteDTO);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClienteDTO> updateCliente(@PathVariable Long id,
      @RequestBody ClienteDTO clienteDTO) {
    ClienteDTO updatedClienteDTO = clienteService.updateCliente(id, clienteDTO);
    return ResponseEntity.ok(updatedClienteDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCliente(@PathVariable Long id) {
    try {
      clienteService.deleteCliente(id);
      return ResponseEntity.noContent().build();
    } catch (ClienteAlreadyDeletedException e) {
      return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
    }
  }

  @GetMapping("/cuenta/{identificacion}")
  public ResponseEntity<Long> getClientePorIdentificacion(@PathVariable String identificacion) {
    Long clienteId = clienteService.obtenerClienteIdPorIdentificacion(identificacion);
    return ResponseEntity.ok(clienteId);
  }
}
