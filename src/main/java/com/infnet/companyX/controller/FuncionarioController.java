package com.infnet.companyX.controller;

import com.infnet.companyX.entity.Funcionario;
import com.infnet.companyX.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodos() {
        List<Funcionario> funcionarios = funcionarioRepository.findByAtivoTrue();
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Long id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        if (funcionario.isPresent() && funcionario.get().getAtivo()) {
            return ResponseEntity.ok(funcionario.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/departamento/{departamento}")
    public ResponseEntity<List<Funcionario>> buscarPorDepartamento(@PathVariable String departamento) {
        List<Funcionario> funcionarios = funcionarioRepository.findByDepartamento(departamento);
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/cargo/{cargo}")
    public ResponseEntity<List<Funcionario>> buscarPorCargo(@PathVariable String cargo) {
        List<Funcionario> funcionarios = funcionarioRepository.findByCargo(cargo);
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Funcionario> buscarPorEmail(@PathVariable String email) {
        Optional<Funcionario> funcionario = funcionarioRepository.findByEmail(email);
        if (funcionario.isPresent() && funcionario.get().getAtivo()) {
            return ResponseEntity.ok(funcionario.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Funcionario> criar(@RequestBody Funcionario funcionario) {
        try {
            if (funcionarioRepository.findByEmail(funcionario.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (funcionario.getCpf() != null && funcionarioRepository.findByCpf(funcionario.getCpf()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            funcionario.setAtivo(true);
            Funcionario novoFuncionario = funcionarioRepository.save(funcionario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoFuncionario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(@PathVariable Long id, @RequestBody Funcionario funcionarioAtualizado) {
        Optional<Funcionario> funcionarioExistente = funcionarioRepository.findById(id);
        
        if (!funcionarioExistente.isPresent() || !funcionarioExistente.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Funcionario funcionario = funcionarioExistente.get();
            
            Optional<Funcionario> funcionarioComEmail = funcionarioRepository.findByEmail(funcionarioAtualizado.getEmail());
            if (funcionarioComEmail.isPresent() && !funcionarioComEmail.get().getId().equals(id)) {
                return ResponseEntity.badRequest().build();
            }
            
            if (funcionarioAtualizado.getCpf() != null) {
                Optional<Funcionario> funcionarioComCpf = funcionarioRepository.findByCpf(funcionarioAtualizado.getCpf());
                if (funcionarioComCpf.isPresent() && !funcionarioComCpf.get().getId().equals(id)) {
                    return ResponseEntity.badRequest().build();
                }
            }
            
            funcionario.setNome(funcionarioAtualizado.getNome());
            funcionario.setEmail(funcionarioAtualizado.getEmail());
            funcionario.setCargo(funcionarioAtualizado.getCargo());
            funcionario.setDepartamento(funcionarioAtualizado.getDepartamento());
            funcionario.setSalario(funcionarioAtualizado.getSalario());
            funcionario.setDataAdmissao(funcionarioAtualizado.getDataAdmissao());
            funcionario.setTelefone(funcionarioAtualizado.getTelefone());
            funcionario.setCpf(funcionarioAtualizado.getCpf());
            
            Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
            return ResponseEntity.ok(funcionarioSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        
        if (!funcionario.isPresent() || !funcionario.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        Funcionario funcionarioParaExcluir = funcionario.get();
        funcionarioParaExcluir.setAtivo(false);
        funcionarioRepository.save(funcionarioParaExcluir);
        
        return ResponseEntity.noContent().build();
    }
}
