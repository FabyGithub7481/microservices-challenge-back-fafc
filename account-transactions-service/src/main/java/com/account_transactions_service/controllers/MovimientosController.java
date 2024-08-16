package com.account_transactions_service.controllers;

import com.account_transactions_service.dtos.MovimientosDTO;
import com.account_transactions_service.exceptions.SaldoInsuficienteException;
import com.account_transactions_service.service.IMovimientosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


 /**
 *  movimientos controller
 *
 */
@RestController
@RequestMapping("/api/movimientos")
public class MovimientosController {

  private final IMovimientosService movimientosService;

  public MovimientosController(IMovimientosService movimientosService) {
    this.movimientosService = movimientosService;
  }


  /**
   * get all movimientos
   *
   * @return {@link ResponseEntity}
   * @see ResponseEntity
   * @see List
   */
  @GetMapping
  public ResponseEntity<List<MovimientosDTO>> getAllMovimientos() {
    List<MovimientosDTO> movimientosDTOs = movimientosService.findAll();
    return new ResponseEntity<>(movimientosDTOs, HttpStatus.OK);
  }

  /**
   * get movimientos by id
   *
   * @param id id
   * @return {@link ResponseEntity}
   * @see ResponseEntity
   * @see MovimientosDTO
   */
  @GetMapping("/{id}")
  public ResponseEntity<MovimientosDTO> getMovimientosById(@PathVariable Long id) {
    Optional<MovimientosDTO> movimientosDTO = movimientosService.findById(id);
    return movimientosDTO.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * create movimientos
   *
   * @param movimientosDTO movimientosDTO
   * @return {@link ResponseEntity}
   * @see ResponseEntity
   * @see MovimientosDTO
   */
  @PostMapping
  public ResponseEntity<MovimientosDTO> createMovimientos(
      @RequestBody MovimientosDTO movimientosDTO) {
    MovimientosDTO nuevoMovimientoDTO = movimientosService.save(movimientosDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimientoDTO);
  }


  /**
   * update movimientos
   *
   * @param id id
   * @param movimientosDTO movimientosDTO
   * @return {@link ResponseEntity}
   * @see ResponseEntity
   * @see MovimientosDTO
   */
  @PutMapping("/{id}")
  public ResponseEntity<MovimientosDTO> updateMovimientos(@PathVariable Long id,
      @RequestBody MovimientosDTO movimientosDTO) {
    MovimientosDTO updatedMovimientosDTO = movimientosService.update(id, movimientosDTO);
    return updatedMovimientosDTO != null ? ResponseEntity.ok(updatedMovimientosDTO)
        : ResponseEntity.notFound().build();
  }

  /**
   * delete movimientos
   *
   * @param id id
   * @return {@link ResponseEntity}
   * @see ResponseEntity
   * @see String
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteMovimientos(@PathVariable Long id) {
   return movimientosService.deleteById(id);

  }
}
