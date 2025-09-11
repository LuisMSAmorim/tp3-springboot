package com.infnet.companyX.controller;

import com.infnet.companyX.entity.Fornecedor;
import com.infnet.companyX.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fornecedores")
@CrossOrigin(origins = "*")
public class FornecedorController {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @GetMapping
    public ResponseEntity<List<Fornecedor>> listarTodos() {
        List<Fornecedor> fornecedores = fornecedorRepository.findByAtivoTrue();
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscarPorId(@PathVariable Long id) {
        Optional<Fornecedor> fornecedor = fornecedorRepository.findById(id);
        if (fornecedor.isPresent() && fornecedor.get().getAtivo()) {
            return ResponseEntity.ok(fornecedor.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Fornecedor> buscarPorCnpj(@PathVariable String cnpj) {
        Optional<Fornecedor> fornecedor = fornecedorRepository.findByCnpj(cnpj);
        if (fornecedor.isPresent() && fornecedor.get().getAtivo()) {
            return ResponseEntity.ok(fornecedor.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<Fornecedor>> buscarPorCidade(@PathVariable String cidade) {
        List<Fornecedor> fornecedores = fornecedorRepository.findByCidade(cidade);
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Fornecedor>> buscarPorEstado(@PathVariable String estado) {
        List<Fornecedor> fornecedores = fornecedorRepository.findByEstado(estado);
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Fornecedor> buscarPorEmail(@PathVariable String email) {
        Optional<Fornecedor> fornecedor = fornecedorRepository.findByEmail(email);
        if (fornecedor.isPresent() && fornecedor.get().getAtivo()) {
            return ResponseEntity.ok(fornecedor.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Fornecedor> criar(@RequestBody Fornecedor fornecedor) {
        try {
            if (fornecedorRepository.findByCnpj(fornecedor.getCnpj()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (fornecedorRepository.findByEmail(fornecedor.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            fornecedor.setAtivo(true);
            Fornecedor novoFornecedor = fornecedorRepository.save(fornecedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoFornecedor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> atualizar(@PathVariable Long id, @RequestBody Fornecedor fornecedorAtualizado) {
        Optional<Fornecedor> fornecedorExistente = fornecedorRepository.findById(id);
        
        if (!fornecedorExistente.isPresent() || !fornecedorExistente.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Fornecedor fornecedor = fornecedorExistente.get();
            
            Optional<Fornecedor> fornecedorComCnpj = fornecedorRepository.findByCnpj(fornecedorAtualizado.getCnpj());
            if (fornecedorComCnpj.isPresent() && !fornecedorComCnpj.get().getId().equals(id)) {
                return ResponseEntity.badRequest().build();
            }
            
            Optional<Fornecedor> fornecedorComEmail = fornecedorRepository.findByEmail(fornecedorAtualizado.getEmail());
            if (fornecedorComEmail.isPresent() && !fornecedorComEmail.get().getId().equals(id)) {
                return ResponseEntity.badRequest().build();
            }
            
            fornecedor.setNome(fornecedorAtualizado.getNome());
            fornecedor.setRazaoSocial(fornecedorAtualizado.getRazaoSocial());
            fornecedor.setCnpj(fornecedorAtualizado.getCnpj());
            fornecedor.setEmail(fornecedorAtualizado.getEmail());
            fornecedor.setTelefone(fornecedorAtualizado.getTelefone());
            fornecedor.setEndereco(fornecedorAtualizado.getEndereco());
            fornecedor.setCidade(fornecedorAtualizado.getCidade());
            fornecedor.setEstado(fornecedorAtualizado.getEstado());
            fornecedor.setCep(fornecedorAtualizado.getCep());
            fornecedor.setPessoaContato(fornecedorAtualizado.getPessoaContato());
            fornecedor.setTelefoneContato(fornecedorAtualizado.getTelefoneContato());
            
            Fornecedor fornecedorSalvo = fornecedorRepository.save(fornecedor);
            return ResponseEntity.ok(fornecedorSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Optional<Fornecedor> fornecedor = fornecedorRepository.findById(id);
        
        if (!fornecedor.isPresent() || !fornecedor.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        Fornecedor fornecedorParaExcluir = fornecedor.get();
        fornecedorParaExcluir.setAtivo(false);
        fornecedorRepository.save(fornecedorParaExcluir);
        
        return ResponseEntity.noContent().build();
    }
}
