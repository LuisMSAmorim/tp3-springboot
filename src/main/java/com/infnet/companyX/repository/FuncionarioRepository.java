package com.infnet.companyX.repository;

import com.infnet.companyX.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    
    Optional<Funcionario> findByEmail(String email);
    
    Optional<Funcionario> findByCpf(String cpf);
    
    List<Funcionario> findByDepartamento(String departamento);
    
    List<Funcionario> findByCargo(String cargo);
    
    List<Funcionario> findByAtivoTrue();
    
    @Query("SELECT f FROM Funcionario f WHERE f.nome LIKE %:nome% AND f.ativo = true")
    List<Funcionario> findByNomeContaining(@Param("nome") String nome);
}
