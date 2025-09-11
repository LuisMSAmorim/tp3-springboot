package com.infnet.companyX.repository;

import com.infnet.companyX.entity.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    
    List<Projeto> findByStatus(Projeto.StatusProjeto status);
    
    List<Projeto> findByClienteId(Long clienteId);
    
    List<Projeto> findByGerenteId(Long gerenteId);
    
    List<Projeto> findByAtivoTrue();
    
    @Query("SELECT p FROM Projeto p WHERE p.nome LIKE %:nome% AND p.ativo = true")
    List<Projeto> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT p FROM Projeto p WHERE p.dataFimPrevista BETWEEN :dataInicio AND :dataFim AND p.ativo = true")
    List<Projeto> findByPrazoEntrega(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT p FROM Projeto p WHERE p.prioridade = :prioridade AND p.ativo = true ORDER BY p.dataFimPrevista ASC")
    List<Projeto> findByPrioridade(@Param("prioridade") Integer prioridade);
    
    @Query("SELECT p FROM Projeto p WHERE p.status = 'EM_ANDAMENTO' AND p.dataFimPrevista < :hoje")
    List<Projeto> findProjetosAtrasados(@Param("hoje") LocalDate hoje);
}
