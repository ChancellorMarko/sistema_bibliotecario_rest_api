package com.example.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valor;

    @Column(nullable = false)
    private String formaPagamento; // Ex: PIX, Dinheiro, Cartão

    @Column(nullable = false)
    private String status; // Pendente, Pago, Cancelado

    @Column(nullable = false)
    private LocalDateTime dataPagamento;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}