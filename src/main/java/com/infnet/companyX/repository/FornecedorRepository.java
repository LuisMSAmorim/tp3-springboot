package com.infnet.companyX.repository;

import com.infnet.companyX.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    
    Optional<Fornecedor> findByCnpj(String cnpj);
    
    Optional<Fornecedor> findByEmail(String email);
    
    List<Fornecedor> findByCidade(String cidade);
    
    List<Fornecedor> findByEstado(String estado);
    
    List<Fornecedor> findByAtivoTrue();
    
    @Query("SELECT f FROM Fornecedor f WHERE f.nome LIKE %:nome% AND f.ativo = true")
    List<Fornecedor> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT f FROM Fornecedor f WHERE f.razaoSocial LIKE %:razaoSocial% AND f.ativo = true")
    List<Fornecedor> findByRazaoSocialContaining(@Param("razaoSocial") String razaoSocial);
}
