package com.account_transactions_service.controllers;

import com.account_transactions_service.dtos.CuentaDTO;
import com.account_transactions_service.service.ICuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 *  cuenta controller
 *
 */
@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

  private final ICuentaService cuentaService;

  public CuentaController(ICuentaService cuentaService) {
    this.cuentaService = cuentaService;
  }

  /**
   * create cuenta
   *
   * @param identificacion identificacion
   * @param cuentaDTO cuentaDTO
   * @return {@link ResponseEntity}
   * @see ResponseEntity
   * @see CuentaDTO
   */
  @PostMapping
  public ResponseEntity<CuentaDTO> createCuenta(@RequestParam String identificacion,
      @RequestBody CuentaDTO cuentaDTO) {
    CuentaDTO nuevaCuentaDTO = cuentaService.createCuenta(identificacion, cuentaDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuentaDTO);

  }

  /**
   * get all cuentas
   *
   * @return {@link ResponseEntity}
   * @see ResponseEntity
   * @see List
   */
  @GetMapping
  public ResponseEntity<List<CuentaDTO>> getAllCuentas() {
    List<CuentaDTO> cuentasDTOs = cuentaService.getAllCuentas();
    return new ResponseEntity<>(cuentasDTOs, HttpStatus.OK);
  }


  //ejemplo de swager para llevar la documentacion de los metodos
  @Operation(summary = "Get a Cuenta by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cuenta encontrada",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = CuentaDTO.class))}),
      @ApiResponse(responseCode = "400", description = "Bad request",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
          content = @Content)})
  @GetMapping("/{id}")
  public ResponseEntity<CuentaDTO> getCuentaById(@PathVariable String id) {
    Optional<CuentaDTO> cuentaDTO = cuentaService.getCuentaById(id);
    return cuentaDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<CuentaDTO> updateCuenta(@PathVariable String id,
      @RequestBody CuentaDTO cuentaDTO) {
    CuentaDTO updatedCuentaDTO = cuentaService.updateCuenta(id, cuentaDTO);
    return updatedCuentaDTO != null ? ResponseEntity.ok(updatedCuentaDTO)
        : ResponseEntity.notFound().build();
  }

  /**
   * delete cuenta
   *
   * @param id id
   * @return {@link ResponseEntity}
   * @see ResponseEntity
   * @see String
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCuenta(@PathVariable String id) {
    return cuentaService.deleteCuenta(id);
  }
}
