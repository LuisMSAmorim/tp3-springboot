package com.infnet.companyX.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infnet.companyX.entity.Produto;
import com.infnet.companyX.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto produtoTeste;

    @BeforeEach
    void setUp() {
        produtoRepository.deleteAll();
        
        produtoTeste = new Produto();
        produtoTeste.setNome("Notebook Dell");
        produtoTeste.setDescricao("Notebook para desenvolvimento");
        produtoTeste.setCodigo("DELL001");
        produtoTeste.setPreco(new BigDecimal("3500.00"));
        produtoTeste.setQuantidadeEstoque(10);
        produtoTeste.setCategoria("Informática");
        produtoTeste.setMarca("Dell");
        produtoTeste.setPeso(new BigDecimal("2.5"));
        produtoTeste.setAtivo(true);
    }

    @Test
    void deveListarTodosProdutos() throws Exception {
        produtoRepository.save(produtoTeste);

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Notebook Dell")))
                .andExpect(jsonPath("$[0].codigo", is("DELL001")))
                .andExpect(jsonPath("$[0].categoria", is("Informática")));
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        Produto salvo = produtoRepository.save(produtoTeste);

        mockMvc.perform(get("/api/produtos/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome", is("Notebook Dell")))
                .andExpect(jsonPath("$.preco", is(3500.00)));
    }

    @Test
    void deveBuscarProdutoPorCodigo() throws Exception {
        produtoRepository.save(produtoTeste);

        mockMvc.perform(get("/api/produtos/codigo/{codigo}", "DELL001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Notebook Dell")));
    }

    @Test
    void deveBuscarProdutosPorCategoria() throws Exception {
        produtoRepository.save(produtoTeste);
        
        Produto produto2 = new Produto();
        produto2.setNome("Mouse Dell");
        produto2.setCodigo("DELL002");
        produto2.setPreco(new BigDecimal("50.00"));
        produto2.setCategoria("Informática");
        produto2.setAtivo(true);
        produtoRepository.save(produto2);

        mockMvc.perform(get("/api/produtos/categoria/{categoria}", "Informática"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveBuscarProdutosPorMarca() throws Exception {
        produtoRepository.save(produtoTeste);

        mockMvc.perform(get("/api/produtos/marca/{marca}", "Dell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].marca", is("Dell")));
    }

    @Test
    void deveBuscarProdutosPorFaixaPreco() throws Exception {
        produtoRepository.save(produtoTeste);

        mockMvc.perform(get("/api/produtos/preco")
                        .param("min", "1000")
                        .param("max", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveBuscarProdutosComEstoqueBaixo() throws Exception {
        produtoTeste.setQuantidadeEstoque(5);
        produtoRepository.save(produtoTeste);

        mockMvc.perform(get("/api/produtos/estoque-baixo")
                        .param("limite", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveCriarNovoProduto() throws Exception {
        String produtoJson = objectMapper.writeValueAsString(produtoTeste);

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Notebook Dell")))
                .andExpect(jsonPath("$.codigo", is("DELL001")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void naoDeveCriarProdutoComCodigoDuplicado() throws Exception {
        produtoRepository.save(produtoTeste);
        
        Produto produtoDuplicado = new Produto();
        produtoDuplicado.setNome("Produto Diferente");
        produtoDuplicado.setCodigo("DELL001");
        produtoDuplicado.setPreco(new BigDecimal("1000.00"));
        produtoDuplicado.setAtivo(true);

        String produtoJson = objectMapper.writeValueAsString(produtoDuplicado);

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarProduto() throws Exception {
        Produto salvo = produtoRepository.save(produtoTeste);
        
        salvo.setNome("Notebook Dell Inspiron");
        salvo.setPreco(new BigDecimal("3800.00"));
        salvo.setQuantidadeEstoque(15);

        String produtoJson = objectMapper.writeValueAsString(salvo);

        mockMvc.perform(put("/api/produtos/{id}", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Notebook Dell Inspiron")))
                .andExpect(jsonPath("$.preco", is(3800.00)))
                .andExpect(jsonPath("$.quantidadeEstoque", is(15)));
    }

    @Test
    void deveRetornar404AoAtualizarProdutoInexistente() throws Exception {
        String produtoJson = objectMapper.writeValueAsString(produtoTeste);

        mockMvc.perform(put("/api/produtos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deveExcluirProduto() throws Exception {
        Produto salvo = produtoRepository.save(produtoTeste);

        mockMvc.perform(delete("/api/produtos/{id}", salvo.getId()))
                .andExpect(status().isNoContent());

        Produto produtoExcluido = produtoRepository.findById(salvo.getId()).orElse(null);
        assert produtoExcluido != null;
        assert !produtoExcluido.getAtivo();
    }

    @Test
    void deveRetornar404AoExcluirProdutoInexistente() throws Exception {
        mockMvc.perform(delete("/api/produtos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404ParaProdutoInexistente() throws Exception {
        mockMvc.perform(get("/api/produtos/999"))
                .andExpect(status().isNotFound());
    }
}