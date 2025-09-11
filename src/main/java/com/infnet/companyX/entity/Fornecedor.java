package com.infnet.companyX.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fornecedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fornecedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(name = "razao_social")
    private String razaoSocial;
    
    @Column(unique = true, nullable = false)
    private String cnpj;
    
    @Column(nullable = false)
    private String email;
    
    private String telefone;
    
    private String endereco;
    
    private String cidade;
    
    private String estado;
    
    private String cep;
    
    @Column(name = "pessoa_contato")
    private String pessoaContato;
    
    @Column(name = "telefone_contato")
    private String telefoneContato;
    
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;
    
    private Boolean ativo = true;
    
    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }
}
