package com.infnet.companyX.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infnet.companyX.entity.Funcionario;
import com.infnet.companyX.repository.FuncionarioRepository;
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
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    private Funcionario funcionarioTeste;

    @BeforeEach
    void setUp() {
        funcionarioRepository.deleteAll();
        
        funcionarioTeste = new Funcionario();
        funcionarioTeste.setNome("João Silva");
        funcionarioTeste.setEmail("joao@empresa.com");
        funcionarioTeste.setCargo("Desenvolvedor");
        funcionarioTeste.setDepartamento("TI");
        funcionarioTeste.setSalario(new BigDecimal("5000.00"));
        funcionarioTeste.setDataAdmissao(LocalDate.of(2024, 1, 15));
        funcionarioTeste.setTelefone("(11) 99999-9999");
        funcionarioTeste.setCpf("123.456.789-01");
        funcionarioTeste.setAtivo(true);
    }

    @Test
    void deveListarTodosFuncionarios() throws Exception {
        funcionarioRepository.save(funcionarioTeste);

        mockMvc.perform(get("/api/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("João Silva")))
                .andExpect(jsonPath("$[0].email", is("joao@empresa.com")))
                .andExpect(jsonPath("$[0].cargo", is("Desenvolvedor")))
                .andExpect(jsonPath("$[0].departamento", is("TI")));
    }

    @Test
    void deveBuscarFuncionarioPorId() throws Exception {
        Funcionario salvo = funcionarioRepository.save(funcionarioTeste);

        mockMvc.perform(get("/api/funcionarios/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome", is("João Silva")))
                .andExpect(jsonPath("$.email", is("joao@empresa.com")))
                .andExpect(jsonPath("$.salario", is(5000.00)));
    }

    @Test
    void deveRetornar404ParaFuncionarioInexistente() throws Exception {
        mockMvc.perform(get("/api/funcionarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarFuncionarioPorEmail() throws Exception {
        funcionarioRepository.save(funcionarioTeste);

        mockMvc.perform(get("/api/funcionarios/email/{email}", "joao@empresa.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("João Silva")));
    }

    @Test
    void deveBuscarFuncionariosPorDepartamento() throws Exception {
        funcionarioRepository.save(funcionarioTeste);
        
        Funcionario funcionario2 = new Funcionario();
        funcionario2.setNome("Maria Santos");
        funcionario2.setEmail("maria@empresa.com");
        funcionario2.setCargo("Analista");
        funcionario2.setDepartamento("TI");
        funcionario2.setSalario(new BigDecimal("4500.00"));
        funcionario2.setAtivo(true);
        funcionarioRepository.save(funcionario2);

        mockMvc.perform(get("/api/funcionarios/departamento/{departamento}", "TI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveCriarNovoFuncionario() throws Exception {
        String funcionarioJson = objectMapper.writeValueAsString(funcionarioTeste);

        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("João Silva")))
                .andExpect(jsonPath("$.email", is("joao@empresa.com")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void naoDeveCriarFuncionarioComEmailDuplicado() throws Exception {
        funcionarioRepository.save(funcionarioTeste);
        
        Funcionario funcionarioDuplicado = new Funcionario();
        funcionarioDuplicado.setNome("Pedro Silva");
        funcionarioDuplicado.setEmail("joao@empresa.com"); 
        funcionarioDuplicado.setCargo("Analista");
        funcionarioDuplicado.setDepartamento("RH");
        funcionarioDuplicado.setAtivo(true);

        String funcionarioJson = objectMapper.writeValueAsString(funcionarioDuplicado);

        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarFuncionarioComCpfDuplicado() throws Exception {
        funcionarioRepository.save(funcionarioTeste);
        
        Funcionario funcionarioDuplicado = new Funcionario();
        funcionarioDuplicado.setNome("Pedro Silva");
        funcionarioDuplicado.setEmail("pedro@empresa.com");
        funcionarioDuplicado.setCargo("Analista");
        funcionarioDuplicado.setDepartamento("RH");
        funcionarioDuplicado.setCpf("123.456.789-01"); 
        funcionarioDuplicado.setAtivo(true);

        String funcionarioJson = objectMapper.writeValueAsString(funcionarioDuplicado);

        mockMvc.perform(post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarFuncionario() throws Exception {
        Funcionario salvo = funcionarioRepository.save(funcionarioTeste);
        
        salvo.setNome("João Santos Silva");
        salvo.setCargo("Tech Lead");
        salvo.setSalario(new BigDecimal("7500.00"));

        String funcionarioJson = objectMapper.writeValueAsString(salvo);

        mockMvc.perform(put("/api/funcionarios/{id}", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("João Santos Silva")))
                .andExpect(jsonPath("$.cargo", is("Tech Lead")))
                .andExpect(jsonPath("$.salario", is(7500.00)));
    }

    @Test
    void deveRetornar404AoAtualizarFuncionarioInexistente() throws Exception {
        String funcionarioJson = objectMapper.writeValueAsString(funcionarioTeste);

        mockMvc.perform(put("/api/funcionarios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(funcionarioJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deveExcluirFuncionario() throws Exception {
        Funcionario salvo = funcionarioRepository.save(funcionarioTeste);

        mockMvc.perform(delete("/api/funcionarios/{id}", salvo.getId()))
                .andExpect(status().isNoContent());

        Funcionario funcionarioExcluido = funcionarioRepository.findById(salvo.getId()).orElse(null);
        assert funcionarioExcluido != null;
        assert !funcionarioExcluido.getAtivo();
    }

    @Test
    void deveRetornar404AoExcluirFuncionarioInexistente() throws Exception {
        mockMvc.perform(delete("/api/funcionarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarFuncionariosPorCargo() throws Exception {
        funcionarioRepository.save(funcionarioTeste);

        mockMvc.perform(get("/api/funcionarios/cargo/{cargo}", "Desenvolvedor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cargo", is("Desenvolvedor")));
    }
}
