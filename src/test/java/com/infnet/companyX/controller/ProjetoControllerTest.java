package com.infnet.companyX.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infnet.companyX.entity.Cliente;
import com.infnet.companyX.entity.Funcionario;
import com.infnet.companyX.entity.Projeto;
import com.infnet.companyX.repository.ClienteRepository;
import com.infnet.companyX.repository.FuncionarioRepository;
import com.infnet.companyX.repository.ProjetoRepository;
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
class ProjetoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    private Projeto projetoTeste;
    private Cliente clienteTeste;
    private Funcionario funcionarioTeste;

    @BeforeEach
    void setUp() {
        projetoRepository.deleteAll();
        clienteRepository.deleteAll();
        funcionarioRepository.deleteAll();
        
        clienteTeste = new Cliente();
        clienteTeste.setNome("Empresa ABC");
        clienteTeste.setEmail("contato@abc.com");
        clienteTeste.setTipoPessoa(Cliente.TipoPessoa.JURIDICA);
        clienteTeste.setCnpj("12.345.678/0001-99");
        clienteTeste.setAtivo(true);
        clienteTeste = clienteRepository.save(clienteTeste);

        funcionarioTeste = new Funcionario();
        funcionarioTeste.setNome("João Gerente");
        funcionarioTeste.setEmail("joao@empresa.com");
        funcionarioTeste.setCargo("Gerente de Projetos");
        funcionarioTeste.setDepartamento("TI");
        funcionarioTeste.setAtivo(true);
        funcionarioTeste = funcionarioRepository.save(funcionarioTeste);

        projetoTeste = new Projeto();
        projetoTeste.setNome("Sistema de Vendas");
        projetoTeste.setDescricao("Desenvolvimento de sistema e-commerce");
        projetoTeste.setCliente(clienteTeste);
        projetoTeste.setGerente(funcionarioTeste);
        projetoTeste.setStatus(Projeto.StatusProjeto.PLANEJAMENTO);
        projetoTeste.setDataInicio(LocalDate.of(2024, 2, 1));
        projetoTeste.setDataFimPrevista(LocalDate.of(2024, 8, 30));
        projetoTeste.setOrcamento(new BigDecimal("85000.00"));
        projetoTeste.setCustoAtual(new BigDecimal("0.00"));
        projetoTeste.setPrioridade(1);
        projetoTeste.setAtivo(true);
    }

    @Test
    void deveListarTodosProjetos() throws Exception {
        projetoRepository.save(projetoTeste);

        mockMvc.perform(get("/api/projetos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Sistema de Vendas")))
                .andExpect(jsonPath("$[0].status", is("PLANEJAMENTO")));
    }

    @Test
    void deveBuscarProjetoPorId() throws Exception {
        Projeto salvo = projetoRepository.save(projetoTeste);

        mockMvc.perform(get("/api/projetos/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome", is("Sistema de Vendas")))
                .andExpect(jsonPath("$.descricao", is("Desenvolvimento de sistema e-commerce")))
                .andExpect(jsonPath("$.orcamento", is(85000.00)));
    }

    @Test
    void deveBuscarProjetosPorStatus() throws Exception {
        projetoRepository.save(projetoTeste);

        mockMvc.perform(get("/api/projetos/status/{status}", "PLANEJAMENTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PLANEJAMENTO")));
    }

    @Test
    void deveBuscarProjetosPorCliente() throws Exception {
        projetoRepository.save(projetoTeste);

        mockMvc.perform(get("/api/projetos/cliente/{clienteId}", clienteTeste.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cliente.id", is(clienteTeste.getId().intValue())));
    }

    @Test
    void deveBuscarProjetosPorGerente() throws Exception {
        projetoRepository.save(projetoTeste);

        mockMvc.perform(get("/api/projetos/gerente/{gerenteId}", funcionarioTeste.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gerente.id", is(funcionarioTeste.getId().intValue())));
    }

    @Test
    void deveBuscarProjetosPorPrioridade() throws Exception {
        projetoRepository.save(projetoTeste);

        mockMvc.perform(get("/api/projetos/prioridade/{prioridade}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].prioridade", is(1)));
    }

    @Test
    void deveBuscarProjetosAtrasados() throws Exception {
        projetoTeste.setStatus(Projeto.StatusProjeto.EM_ANDAMENTO);
        projetoTeste.setDataFimPrevista(LocalDate.now().minusDays(10)); 
        projetoRepository.save(projetoTeste);

        mockMvc.perform(get("/api/projetos/atrasados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveBuscarProjetosPorPrazoEntrega() throws Exception {
        projetoRepository.save(projetoTeste);

        mockMvc.perform(get("/api/projetos/prazo")
                        .param("dataInicio", "2024-08-01")
                        .param("dataFim", "2024-09-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveCriarNovoProjeto() throws Exception {
        String projetoJson = objectMapper.writeValueAsString(projetoTeste);

        mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Sistema de Vendas")))
                .andExpect(jsonPath("$.status", is("PLANEJAMENTO")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void naoDeveCriarProjetoComClienteInexistente() throws Exception {
        Cliente clienteInexistente = new Cliente();
        clienteInexistente.setId(999L);
        projetoTeste.setCliente(clienteInexistente);

        String projetoJson = objectMapper.writeValueAsString(projetoTeste);

        mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarProjetoComGerenteInexistente() throws Exception {
        Funcionario gerenteInexistente = new Funcionario();
        gerenteInexistente.setId(999L);
        projetoTeste.setGerente(gerenteInexistente);

        String projetoJson = objectMapper.writeValueAsString(projetoTeste);

        mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarProjeto() throws Exception {
        Projeto salvo = projetoRepository.save(projetoTeste);
        
        salvo.setNome("Sistema de Vendas Online");
        salvo.setStatus(Projeto.StatusProjeto.EM_ANDAMENTO);
        salvo.setCustoAtual(new BigDecimal("15000.00"));

        String projetoJson = objectMapper.writeValueAsString(salvo);

        mockMvc.perform(put("/api/projetos/{id}", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Sistema de Vendas Online")))
                .andExpect(jsonPath("$.status", is("EM_ANDAMENTO")))
                .andExpect(jsonPath("$.custoAtual", is(15000.00)));
    }

    @Test
    void deveRetornar404AoAtualizarProjetoInexistente() throws Exception {
        String projetoJson = objectMapper.writeValueAsString(projetoTeste);

        mockMvc.perform(put("/api/projetos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deveExcluirProjeto() throws Exception {
        Projeto salvo = projetoRepository.save(projetoTeste);

        mockMvc.perform(delete("/api/projetos/{id}", salvo.getId()))
                .andExpect(status().isNoContent());

        Projeto projetoExcluido = projetoRepository.findById(salvo.getId()).orElse(null);
        assert projetoExcluido != null;
        assert !projetoExcluido.getAtivo();
    }

    @Test
    void deveRetornar404AoExcluirProjetoInexistente() throws Exception {
        mockMvc.perform(delete("/api/projetos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404ParaProjetoInexistente() throws Exception {
        mockMvc.perform(get("/api/projetos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400ParaStatusInvalido() throws Exception {
        mockMvc.perform(get("/api/projetos/status/STATUS_INVALIDO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400ParaDataInvalida() throws Exception {
        mockMvc.perform(get("/api/projetos/prazo")
                        .param("dataInicio", "data-invalida")
                        .param("dataFim", "2024-09-30"))
                .andExpect(status().isBadRequest());
    }
}
