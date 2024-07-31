package com.account_transactions_service.controller;

import com.account_transactions_service.entities.Movimientos;
import com.account_transactions_service.response.MovimientosResponseRest;
import com.account_transactions_service.service.IMovimientosService;
import com.account_transactions_service.service.MovimientosServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientosController {

  @Autowired
  private IMovimientosService movimientosService;

  @GetMapping
  public List<Movimientos> getAllMovimientos() {
    return movimientosService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Movimientos> getMovimientosById(@PathVariable Long id) {
    Optional<Movimientos> movimientos = movimientosService.findById(id);
    return movimientos.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<MovimientosResponseRest> createMovimientos(
      @RequestBody Movimientos movimientos) {
    ResponseEntity<MovimientosResponseRest> response = movimientosService.save(movimientos);
    return response;
  }


  @PutMapping("/{id}")
  public ResponseEntity<Movimientos> updateMovimientos(@PathVariable Long id,
      @RequestBody Movimientos movimientosDetails) {
    Optional<Movimientos> movimientos = movimientosService.findById(id);
    if (movimientos.isPresent()) {
      Movimientos movimientosToUpdate = movimientos.get();
      movimientosToUpdate.setFecha(movimientosDetails.getFecha());
      movimientosToUpdate.setTipoMovimiento(movimientosDetails.getTipoMovimiento());
      movimientosToUpdate.setValor(movimientosDetails.getValor());
      movimientosToUpdate.setSaldo(movimientosDetails.getSaldo());
      movimientosService.save(movimientosToUpdate);
      return ResponseEntity.ok(movimientosToUpdate);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMovimientos(@PathVariable Long id) {
    movimientosService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
