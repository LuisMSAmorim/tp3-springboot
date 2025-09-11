package com.infnet.companyX.controller;

import com.infnet.companyX.entity.Projeto;
import com.infnet.companyX.entity.Cliente;
import com.infnet.companyX.entity.Funcionario;
import com.infnet.companyX.repository.ProjetoRepository;
import com.infnet.companyX.repository.ClienteRepository;
import com.infnet.companyX.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projetos")
@CrossOrigin(origins = "*")
public class ProjetoController {

    @Autowired
    private ProjetoRepository projetoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @GetMapping
    public ResponseEntity<List<Projeto>> listarTodos() {
        List<Projeto> projetos = projetoRepository.findByAtivoTrue();
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable Long id) {
        Optional<Projeto> projeto = projetoRepository.findById(id);
        if (projeto.isPresent() && projeto.get().getAtivo()) {
            return ResponseEntity.ok(projeto.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Projeto>> buscarPorStatus(@PathVariable String status) {
        try {
            Projeto.StatusProjeto statusProjeto = Projeto.StatusProjeto.valueOf(status.toUpperCase());
            List<Projeto> projetos = projetoRepository.findByStatus(statusProjeto);
            return ResponseEntity.ok(projetos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Projeto>> buscarPorCliente(@PathVariable Long clienteId) {
        List<Projeto> projetos = projetoRepository.findByClienteId(clienteId);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/gerente/{gerenteId}")
    public ResponseEntity<List<Projeto>> buscarPorGerente(@PathVariable Long gerenteId) {
        List<Projeto> projetos = projetoRepository.findByGerenteId(gerenteId);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/prioridade/{prioridade}")
    public ResponseEntity<List<Projeto>> buscarPorPrioridade(@PathVariable Integer prioridade) {
        List<Projeto> projetos = projetoRepository.findByPrioridade(prioridade);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<Projeto>> buscarProjetosAtrasados() {
        List<Projeto> projetos = projetoRepository.findProjetosAtrasados(LocalDate.now());
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/prazo")
    public ResponseEntity<List<Projeto>> buscarPorPrazoEntrega(
            @RequestParam String dataInicio, 
            @RequestParam String dataFim) {
        try {
            LocalDate inicio = LocalDate.parse(dataInicio);
            LocalDate fim = LocalDate.parse(dataFim);
            List<Projeto> projetos = projetoRepository.findByPrazoEntrega(inicio, fim);
            return ResponseEntity.ok(projetos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Projeto> criar(@RequestBody Projeto projeto) {
        try {
            if (projeto.getCliente() != null && projeto.getCliente().getId() != null) {
                Optional<Cliente> cliente = clienteRepository.findById(projeto.getCliente().getId());
                if (!cliente.isPresent() || !cliente.get().getAtivo()) {
                    return ResponseEntity.badRequest().build();
                }
                projeto.setCliente(cliente.get());
            }
            
            if (projeto.getGerente() != null && projeto.getGerente().getId() != null) {
                Optional<Funcionario> gerente = funcionarioRepository.findById(projeto.getGerente().getId());
                if (!gerente.isPresent() || !gerente.get().getAtivo()) {
                    return ResponseEntity.badRequest().build();
                }
                projeto.setGerente(gerente.get());
            }
            
            projeto.setAtivo(true);
            Projeto novoProjeto = projetoRepository.save(projeto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProjeto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projeto> atualizar(@PathVariable Long id, @RequestBody Projeto projetoAtualizado) {
        Optional<Projeto> projetoExistente = projetoRepository.findById(id);
        
        if (!projetoExistente.isPresent() || !projetoExistente.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Projeto projeto = projetoExistente.get();
            
            if (projetoAtualizado.getCliente() != null && projetoAtualizado.getCliente().getId() != null) {
                Optional<Cliente> cliente = clienteRepository.findById(projetoAtualizado.getCliente().getId());
                if (!cliente.isPresent() || !cliente.get().getAtivo()) {
                    return ResponseEntity.badRequest().build();
                }
                projeto.setCliente(cliente.get());
            } else {
                projeto.setCliente(null);
            }
            
            if (projetoAtualizado.getGerente() != null && projetoAtualizado.getGerente().getId() != null) {
                Optional<Funcionario> gerente = funcionarioRepository.findById(projetoAtualizado.getGerente().getId());
                if (!gerente.isPresent() || !gerente.get().getAtivo()) {
                    return ResponseEntity.badRequest().build();
                }
                projeto.setGerente(gerente.get());
            } else {
                projeto.setGerente(null);
            }
            
            projeto.setNome(projetoAtualizado.getNome());
            projeto.setDescricao(projetoAtualizado.getDescricao());
            projeto.setStatus(projetoAtualizado.getStatus());
            projeto.setDataInicio(projetoAtualizado.getDataInicio());
            projeto.setDataFimPrevista(projetoAtualizado.getDataFimPrevista());
            projeto.setDataFimReal(projetoAtualizado.getDataFimReal());
            projeto.setOrcamento(projetoAtualizado.getOrcamento());
            projeto.setCustoAtual(projetoAtualizado.getCustoAtual());
            projeto.setPrioridade(projetoAtualizado.getPrioridade());
            
            Projeto projetoSalvo = projetoRepository.save(projeto);
            return ResponseEntity.ok(projetoSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Optional<Projeto> projeto = projetoRepository.findById(id);
        
        if (!projeto.isPresent() || !projeto.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        Projeto projetoParaExcluir = projeto.get();
        projetoParaExcluir.setAtivo(false);
        projetoRepository.save(projetoParaExcluir);
        
        return ResponseEntity.noContent().build();
    }
}
