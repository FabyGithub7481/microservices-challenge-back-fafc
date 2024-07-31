package com.account_transactions_service.controller;


import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.service.CuentaServiceImpl;
import com.account_transactions_service.service.ICuentaService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

  @Autowired
  private ICuentaService cuentaService;

  @PostMapping
  public ResponseEntity<String> createCuenta(@RequestParam String identificacion, @RequestBody Cuenta cuenta) {
    try {
      Cuenta nuevaCuenta = cuentaService.createCuenta(identificacion, cuenta);
      return new ResponseEntity<>("Cuenta con Id: " +nuevaCuenta.getCuentaId()+" y número de cuenta: " + nuevaCuenta.getNumeroCuenta()+" creada con éxito" , HttpStatus.CREATED);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>("Error interno del servidor controlador: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping
  public ResponseEntity<List<Cuenta>> getAllCuentas() {
    List<Cuenta> cuentas = cuentaService.getAllCuentas();
    return new ResponseEntity<>(cuentas, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Cuenta> getCuentaById(@PathVariable Long id) {
    Optional<Cuenta> cuenta = cuentaService.getCuentaById(id);
    return cuenta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Cuenta> updateCuenta(@PathVariable Long id, @RequestBody Cuenta cuenta) {
    Cuenta updatedCuenta = cuentaService.updateCuenta(id, cuenta);
    return updatedCuenta != null ? ResponseEntity.ok(updatedCuenta) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCuenta(@PathVariable Long id) {
    try {
      cuentaService.deleteCuenta(id);
      return new ResponseEntity<>("Cuenta con Id: " + id +" borrada con exito", HttpStatus.OK);
    }catch (RuntimeException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>("Error interno del servidor controlador: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

