package com.infnet.companyX.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String telefone;
    
    @Column(unique = true)
    private String cpf;
    
    @Column(unique = true)
    private String cnpj;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    private TipoPessoa tipoPessoa;
    
    private String endereco;
    
    private String cidade;
    
    private String estado;
    
    private String cep;
    
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;
    
    private Boolean ativo = true;
    
    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }
    
    public enum TipoPessoa {
        FISICA, JURIDICA
    }
}
