package com.account_transactions_service.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "movimientos")
public class Movimientos {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = true)
  private LocalDateTime fecha;

  @Column(nullable = false)
  private String tipoMovimiento;

  @Column(nullable = false)
  private BigDecimal valor;

  @Column(nullable = false)
  private BigDecimal saldo;

  @ManyToOne
  @JoinColumn(name = "cuenta_id")
  private Cuenta cuenta;


}
