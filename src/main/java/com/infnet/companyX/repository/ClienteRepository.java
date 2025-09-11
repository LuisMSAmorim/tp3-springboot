package com.infnet.companyX.repository;

import com.infnet.companyX.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByEmail(String email);
    
    Optional<Cliente> findByCpf(String cpf);
    
    Optional<Cliente> findByCnpj(String cnpj);
    
    List<Cliente> findByTipoPessoa(Cliente.TipoPessoa tipoPessoa);
    
    List<Cliente> findByCidade(String cidade);
    
    List<Cliente> findByEstado(String estado);
    
    List<Cliente> findByAtivoTrue();
    
    @Query("SELECT c FROM Cliente c WHERE c.nome LIKE %:nome% AND c.ativo = true")
    List<Cliente> findByNomeContaining(@Param("nome") String nome);
}
