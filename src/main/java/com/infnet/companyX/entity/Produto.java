package com.infnet.companyX.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(unique = true, nullable = false)
    private String codigo;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;
    
    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque;
    
    private String categoria;
    
    private String marca;
    
    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal peso;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    private Boolean ativo = true;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
}
