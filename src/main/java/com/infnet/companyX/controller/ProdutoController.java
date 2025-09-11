package com.infnet.companyX.controller;

import com.infnet.companyX.entity.Produto;
import com.infnet.companyX.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoRepository.findByAtivoTrue();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isPresent() && produto.get().getAtivo()) {
            return ResponseEntity.ok(produto.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        Optional<Produto> produto = produtoRepository.findByCodigo(codigo);
        if (produto.isPresent() && produto.get().getAtivo()) {
            return ResponseEntity.ok(produto.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoRepository.findByCategoria(categoria);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Produto>> buscarPorMarca(@PathVariable String marca) {
        List<Produto> produtos = produtoRepository.findByMarca(marca);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/preco")
    public ResponseEntity<List<Produto>> buscarPorFaixaPreco(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        List<Produto> produtos = produtoRepository.findByPrecoRange(min, max);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<Produto>> buscarEstoqueBaixo(@RequestParam(defaultValue = "10") Integer limite) {
        List<Produto> produtos = produtoRepository.findEstoqueBaixo(limite);
        return ResponseEntity.ok(produtos);
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        try {
            if (produtoRepository.findByCodigo(produto.getCodigo()).isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            produto.setAtivo(true);
            Produto novoProduto = produtoRepository.save(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produtoAtualizado) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        
        if (!produtoExistente.isPresent() || !produtoExistente.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Produto produto = produtoExistente.get();
            
            Optional<Produto> produtoComCodigo = produtoRepository.findByCodigo(produtoAtualizado.getCodigo());
            if (produtoComCodigo.isPresent() && !produtoComCodigo.get().getId().equals(id)) {
                return ResponseEntity.badRequest().build();
            }
            
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setCodigo(produtoAtualizado.getCodigo());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produto.setCategoria(produtoAtualizado.getCategoria());
            produto.setMarca(produtoAtualizado.getMarca());
            produto.setPeso(produtoAtualizado.getPeso());
            
            Produto produtoSalvo = produtoRepository.save(produto);
            return ResponseEntity.ok(produtoSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        
        if (!produto.isPresent() || !produto.get().getAtivo()) {
            return ResponseEntity.notFound().build();
        }
        
        Produto produtoParaExcluir = produto.get();
        produtoParaExcluir.setAtivo(false);
        produtoRepository.save(produtoParaExcluir);
        
        return ResponseEntity.noContent().build();
    }
}
