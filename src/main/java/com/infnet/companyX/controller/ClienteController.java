package com.infnet.companyX.controller;

import com.infnet.companyX.entity.Cliente;
import com.infnet.companyX.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        List<Cliente> clientes = clienteRepository.findByAtivoTrue();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent() && cliente.get().getAtivo()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Cliente> buscarPorEmail(@PathVariable String email) {
        Optional<Cliente> cliente = clienteRepository.findByEmail(email);
        if (cliente.isPresent() && cliente.get().getAtivo()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarPorCpf(@PathVariable String cpf) {
        Optional<Cliente> cliente = clienteRepository.findByCpf(cpf);
        if (cliente.isPresent() && cliente.get().getAtivo()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Cliente> buscarPorCnpj(@PathVariable String cnpj) {
        Optional<Cliente> cliente = clienteRepository.findByCnpj(cnpj);
        if (cliente.isPresent() && cliente.get().getAtivo()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Cliente>> buscarPorTipoPessoa(@PathVariable String tipo) {
        try {
            Cliente.TipoPessoa tipoPessoa = Cliente.TipoPessoa.valueOf(tipo.toUpperCase());
            List<Cliente> clientes = clienteRepository.findByTipoPessoa(tipoPessoa);
            return ResponseEntity.ok(clientes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<Cliente>> buscarPorCidade(@PathVariable String cidade) {
        List<Cliente> clientes = clienteRepository.findByCidade(cidade);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Cliente>> buscarPorEstado(@PathVariable String estado) {
        List<Cliente> clientes = clienteRepository.findByEstado(estado);
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        try {
            if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (cliente.getCpf() != null && clienteRepository.findByCpf(cliente.getCpf()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (cliente.getCnpj() != null && clienteRepository.findByCnpj(cliente.getCnpj()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (cliente.getTipoPessoa() == Cliente.TipoPessoa.FISICA && cliente.getCpf() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            if (cliente.getTipoPessoa() == Cliente.TipoPessoa.JURIDICA && cliente.getCnpj() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            cliente.setAtivo(true);
            Cliente novoCliente = clienteRepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);
        
        if (!clienteExistente.isPresent() || !clienteExistente.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Cliente cliente = clienteExistente.get();
            
            Optional<Cliente> clienteComEmail = clienteRepository.findByEmail(clienteAtualizado.getEmail());
            if (clienteComEmail.isPresent() && !clienteComEmail.get().getId().equals(id)) {
                return ResponseEntity.badRequest().build();
            }
            
            if (clienteAtualizado.getCpf() != null) {
                Optional<Cliente> clienteComCpf = clienteRepository.findByCpf(clienteAtualizado.getCpf());
                if (clienteComCpf.isPresent() && !clienteComCpf.get().getId().equals(id)) {
                    return ResponseEntity.badRequest().build();
                }
            }
            
            if (clienteAtualizado.getCnpj() != null) {
                Optional<Cliente> clienteComCnpj = clienteRepository.findByCnpj(clienteAtualizado.getCnpj());
                if (clienteComCnpj.isPresent() && !clienteComCnpj.get().getId().equals(id)) {
                    return ResponseEntity.badRequest().build();
                }
            }
            
            cliente.setNome(clienteAtualizado.getNome());
            cliente.setEmail(clienteAtualizado.getEmail());
            cliente.setTelefone(clienteAtualizado.getTelefone());
            cliente.setCpf(clienteAtualizado.getCpf());
            cliente.setCnpj(clienteAtualizado.getCnpj());
            cliente.setTipoPessoa(clienteAtualizado.getTipoPessoa());
            cliente.setEndereco(clienteAtualizado.getEndereco());
            cliente.setCidade(clienteAtualizado.getCidade());
            cliente.setEstado(clienteAtualizado.getEstado());
            cliente.setCep(clienteAtualizado.getCep());
            cliente.setDataNascimento(clienteAtualizado.getDataNascimento());
            
            Cliente clienteSalvo = clienteRepository.save(cliente);
            return ResponseEntity.ok(clienteSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        
        if (!cliente.isPresent() || !cliente.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        Cliente clienteParaExcluir = cliente.get();
        clienteParaExcluir.setAtivo(false);
        clienteRepository.save(clienteParaExcluir);
        
        return ResponseEntity.noContent().build();
    }
}
