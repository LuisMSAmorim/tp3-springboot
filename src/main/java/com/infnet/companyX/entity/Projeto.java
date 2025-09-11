package com.infnet.companyX.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projetos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Projeto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "gerente_id")
    private Funcionario gerente;
    
    @Enumerated(EnumType.STRING)
    private StatusProjeto status;
    
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    
    @Column(name = "data_fim_prevista")
    private LocalDate dataFimPrevista;
    
    @Column(name = "data_fim_real")
    private LocalDate dataFimReal;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal orcamento;
    
    @Column(name = "custo_atual", precision = 12, scale = 2)
    private BigDecimal custoAtual;
    
    private Integer prioridade;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    private Boolean ativo = true;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (status == null) {
            status = StatusProjeto.PLANEJAMENTO;
        }
    }
    
    public enum StatusProjeto {
        PLANEJAMENTO,
        EM_ANDAMENTO,
        PAUSADO,
        CONCLUIDO,
        CANCELADO
    }
}
