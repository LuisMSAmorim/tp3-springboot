package com.infnet.companyX.repository;

import com.infnet.companyX.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    Optional<Produto> findByCodigo(String codigo);
    
    List<Produto> findByCategoria(String categoria);
    
    List<Produto> findByMarca(String marca);
    
    List<Produto> findByAtivoTrue();
    
    @Query("SELECT p FROM Produto p WHERE p.nome LIKE %:nome% AND p.ativo = true")
    List<Produto> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT p FROM Produto p WHERE p.preco BETWEEN :precoMin AND :precoMax AND p.ativo = true")
    List<Produto> findByPrecoRange(@Param("precoMin") BigDecimal precoMin, @Param("precoMax") BigDecimal precoMax);
    
    @Query("SELECT p FROM Produto p WHERE p.quantidadeEstoque < :limite AND p.ativo = true")
    List<Produto> findEstoqueBaixo(@Param("limite") Integer limite);
}
