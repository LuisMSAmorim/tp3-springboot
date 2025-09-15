package com.infnet.companyX.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infnet.companyX.entity.Fornecedor;
import com.infnet.companyX.repository.FornecedorRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FornecedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    private Fornecedor fornecedorTeste;

    @BeforeEach
    void setUp() {
        fornecedorRepository.deleteAll();
        
        fornecedorTeste = new Fornecedor();
        fornecedorTeste.setNome("TechSupply Ltda");
        fornecedorTeste.setRazaoSocial("Tech Supply Soluções em Tecnologia Ltda");
        fornecedorTeste.setCnpj("12345678000199");
        fornecedorTeste.setEmail("contato@techsupply.com");
        fornecedorTeste.setTelefone("(11) 3333-4444");
        fornecedorTeste.setEndereco("Av. Paulista, 1000");
        fornecedorTeste.setCidade("São Paulo");
        fornecedorTeste.setEstado("SP");
        fornecedorTeste.setCep("01310-100");
        fornecedorTeste.setPessoaContato("Maria Santos");
        fornecedorTeste.setTelefoneContato("(11) 99888-7777");
        fornecedorTeste.setAtivo(true);
    }

    @Test
    void deveListarTodosFornecedores() throws Exception {
        fornecedorRepository.save(fornecedorTeste);

        mockMvc.perform(get("/api/fornecedores"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("TechSupply Ltda")))
                .andExpect(jsonPath("$[0].cnpj", is("12345678000199")))
                .andExpect(jsonPath("$[0].cidade", is("São Paulo")));
    }

    @Test
    void deveBuscarFornecedorPorId() throws Exception {
        Fornecedor salvo = fornecedorRepository.save(fornecedorTeste);

        mockMvc.perform(get("/api/fornecedores/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome", is("TechSupply Ltda")))
                .andExpect(jsonPath("$.email", is("contato@techsupply.com")));
    }

    @Test
    void deveBuscarFornecedorPorCnpj() throws Exception {
        fornecedorRepository.save(fornecedorTeste);

        mockMvc.perform(get("/api/fornecedores/cnpj/{cnpj}", "12345678000199"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("TechSupply Ltda")));
    }

    @Test
    void deveBuscarFornecedorPorEmail() throws Exception {
        fornecedorRepository.save(fornecedorTeste);

        mockMvc.perform(get("/api/fornecedores/email/{email}", "contato@techsupply.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("TechSupply Ltda")));
    }

    @Test
    void deveBuscarFornecedoresPorCidade() throws Exception {
        fornecedorRepository.save(fornecedorTeste);
        
        Fornecedor fornecedor2 = new Fornecedor();
        fornecedor2.setNome("SP Tech");
        fornecedor2.setCnpj("98.765.432/0001-88");
        fornecedor2.setEmail("contato@sptech.com");
        fornecedor2.setCidade("São Paulo");
        fornecedor2.setAtivo(true);
        fornecedorRepository.save(fornecedor2);

        mockMvc.perform(get("/api/fornecedores/cidade/{cidade}", "São Paulo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveBuscarFornecedoresPorEstado() throws Exception {
        fornecedorRepository.save(fornecedorTeste);

        mockMvc.perform(get("/api/fornecedores/estado/{estado}", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is("SP")));
    }

    @Test
    void deveCriarNovoFornecedor() throws Exception {
        String fornecedorJson = objectMapper.writeValueAsString(fornecedorTeste);

        mockMvc.perform(post("/api/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("TechSupply Ltda")))
                .andExpect(jsonPath("$.cnpj", is("12345678000199")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void naoDeveCriarFornecedorComCnpjDuplicado() throws Exception {
        fornecedorRepository.save(fornecedorTeste);
        
        Fornecedor fornecedorDuplicado = new Fornecedor();
        fornecedorDuplicado.setNome("Outro Fornecedor");
        fornecedorDuplicado.setCnpj("12345678000199"); 
        fornecedorDuplicado.setEmail("outro@email.com");
        fornecedorDuplicado.setAtivo(true);

        String fornecedorJson = objectMapper.writeValueAsString(fornecedorDuplicado);

        mockMvc.perform(post("/api/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarFornecedorComEmailDuplicado() throws Exception {
        fornecedorRepository.save(fornecedorTeste);
        
        Fornecedor fornecedorDuplicado = new Fornecedor();
        fornecedorDuplicado.setNome("Outro Fornecedor");
        fornecedorDuplicado.setCnpj("98.765.432/0001-88");
        fornecedorDuplicado.setEmail("contato@techsupply.com"); 
        fornecedorDuplicado.setAtivo(true);

        String fornecedorJson = objectMapper.writeValueAsString(fornecedorDuplicado);

        mockMvc.perform(post("/api/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarFornecedor() throws Exception {
        Fornecedor salvo = fornecedorRepository.save(fornecedorTeste);
        
        salvo.setNome("TechSupply Solutions");
        salvo.setTelefone("(11) 4444-5555");
        salvo.setPessoaContato("João Silva");

        String fornecedorJson = objectMapper.writeValueAsString(salvo);

        mockMvc.perform(put("/api/fornecedores/{id}", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("TechSupply Solutions")))
                .andExpect(jsonPath("$.telefone", is("(11) 4444-5555")))
                .andExpect(jsonPath("$.pessoaContato", is("João Silva")));
    }

    @Test
    void deveRetornar404AoAtualizarFornecedorInexistente() throws Exception {
        String fornecedorJson = objectMapper.writeValueAsString(fornecedorTeste);

        mockMvc.perform(put("/api/fornecedores/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deveExcluirFornecedor() throws Exception {
        Fornecedor salvo = fornecedorRepository.save(fornecedorTeste);

        mockMvc.perform(delete("/api/fornecedores/{id}", salvo.getId()))
                .andExpect(status().isNoContent());

        Fornecedor fornecedorExcluido = fornecedorRepository.findById(salvo.getId()).orElse(null);
        assert fornecedorExcluido != null;
        assert !fornecedorExcluido.getAtivo();
    }

    @Test
    void deveRetornar404AoExcluirFornecedorInexistente() throws Exception {
        mockMvc.perform(delete("/api/fornecedores/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404ParaFornecedorInexistente() throws Exception {
        mockMvc.perform(get("/api/fornecedores/999"))
                .andExpect(status().isNotFound());
    }
}
