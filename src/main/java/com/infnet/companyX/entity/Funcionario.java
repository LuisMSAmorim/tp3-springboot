package com.infnet.companyX.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "funcionarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String cargo;
    
    @Column(nullable = false)
    private String departamento;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal salario;
    
    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;
    
    @Column(name = "telefone")
    private String telefone;
    
    @Column(name = "cpf", unique = true)
    private String cpf;
    
    private Boolean ativo = true;
}
