package com.infnet.companyX.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infnet.companyX.entity.Cliente;
import com.infnet.companyX.repository.ClienteRepository;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clientePessoaFisica;
    private Cliente clientePessoaJuridica;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        
        clientePessoaFisica = new Cliente();
        clientePessoaFisica.setNome("Ana Maria Costa");
        clientePessoaFisica.setEmail("ana@email.com");
        clientePessoaFisica.setTelefone("(11) 98888-7777");
        clientePessoaFisica.setCpf("987.654.321-00");
        clientePessoaFisica.setTipoPessoa(Cliente.TipoPessoa.FISICA);
        clientePessoaFisica.setEndereco("Rua das Flores, 123");
        clientePessoaFisica.setCidade("São Paulo");
        clientePessoaFisica.setEstado("SP");
        clientePessoaFisica.setCep("04567-890");
        clientePessoaFisica.setDataNascimento(LocalDate.of(1985, 3, 15));
        clientePessoaFisica.setAtivo(true);

        clientePessoaJuridica = new Cliente();
        clientePessoaJuridica.setNome("Empresa XYZ");
        clientePessoaJuridica.setEmail("contato@xyz.com");
        clientePessoaJuridica.setTelefone("(11) 4444-5555");
        clientePessoaJuridica.setCnpj("98765432000110");
        clientePessoaJuridica.setTipoPessoa(Cliente.TipoPessoa.JURIDICA);
        clientePessoaJuridica.setEndereco("Rua Comercial, 500");
        clientePessoaJuridica.setCidade("São Paulo");
        clientePessoaJuridica.setEstado("SP");
        clientePessoaJuridica.setCep("01234-567");
        clientePessoaJuridica.setAtivo(true);
    }

    @Test
    void deveListarTodosClientes() throws Exception {
        clienteRepository.save(clientePessoaFisica);
        clienteRepository.save(clientePessoaJuridica);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveBuscarClientePorId() throws Exception {
        Cliente salvo = clienteRepository.save(clientePessoaFisica);

        mockMvc.perform(get("/api/clientes/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome", is("Ana Maria Costa")))
                .andExpect(jsonPath("$.email", is("ana@email.com")))
                .andExpect(jsonPath("$.tipoPessoa", is("FISICA")));
    }

    @Test
    void deveBuscarClientePorEmail() throws Exception {
        clienteRepository.save(clientePessoaFisica);

        mockMvc.perform(get("/api/clientes/email/{email}", "ana@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Ana Maria Costa")));
    }

    @Test
    void deveBuscarClientePorCpf() throws Exception {
        clienteRepository.save(clientePessoaFisica);

        mockMvc.perform(get("/api/clientes/cpf/{cpf}", "987.654.321-00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Ana Maria Costa")));
    }

    @Test
    void deveBuscarClientePorCnpj() throws Exception {
        clienteRepository.save(clientePessoaJuridica);

        mockMvc.perform(get("/api/clientes/cnpj/{cnpj}", "98765432000110"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Empresa XYZ")));
    }

    @Test
    void deveBuscarClientesPorTipoPessoa() throws Exception {
        clienteRepository.save(clientePessoaFisica);
        clienteRepository.save(clientePessoaJuridica);

        mockMvc.perform(get("/api/clientes/tipo/{tipo}", "FISICA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoPessoa", is("FISICA")));
    }

    @Test
    void deveBuscarClientesPorCidade() throws Exception {
        clienteRepository.save(clientePessoaFisica);
        clienteRepository.save(clientePessoaJuridica);

        mockMvc.perform(get("/api/clientes/cidade/{cidade}", "São Paulo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveBuscarClientesPorEstado() throws Exception {
        clienteRepository.save(clientePessoaFisica);

        mockMvc.perform(get("/api/clientes/estado/{estado}", "SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is("SP")));
    }

    @Test
    void deveCriarClientePessoaFisica() throws Exception {
        String clienteJson = objectMapper.writeValueAsString(clientePessoaFisica);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Ana Maria Costa")))
                .andExpect(jsonPath("$.cpf", is("987.654.321-00")))
                .andExpect(jsonPath("$.tipoPessoa", is("FISICA")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void deveCriarClientePessoaJuridica() throws Exception {
        String clienteJson = objectMapper.writeValueAsString(clientePessoaJuridica);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Empresa XYZ")))
                .andExpect(jsonPath("$.cnpj", is("98765432000110")))
                .andExpect(jsonPath("$.tipoPessoa", is("JURIDICA")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void naoDeveCriarClienteComEmailDuplicado() throws Exception {
        clienteRepository.save(clientePessoaFisica);
        
        Cliente clienteDuplicado = new Cliente();
        clienteDuplicado.setNome("Outro Cliente");
        clienteDuplicado.setEmail("ana@email.com"); 
        clienteDuplicado.setTipoPessoa(Cliente.TipoPessoa.FISICA);
        clienteDuplicado.setCpf("111.222.333-44");
        clienteDuplicado.setAtivo(true);

        String clienteJson = objectMapper.writeValueAsString(clienteDuplicado);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarClienteComCpfDuplicado() throws Exception {
        clienteRepository.save(clientePessoaFisica);
        
        Cliente clienteDuplicado = new Cliente();
        clienteDuplicado.setNome("Outro Cliente");
        clienteDuplicado.setEmail("outro@email.com");
        clienteDuplicado.setTipoPessoa(Cliente.TipoPessoa.FISICA);
        clienteDuplicado.setCpf("987.654.321-00"); 
        clienteDuplicado.setAtivo(true);

        String clienteJson = objectMapper.writeValueAsString(clienteDuplicado);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarClientePessoaFisicaSemCpf() throws Exception {
        clientePessoaFisica.setCpf(null); 
        String clienteJson = objectMapper.writeValueAsString(clientePessoaFisica);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarClientePessoaJuridicaSemCnpj() throws Exception {
        clientePessoaJuridica.setCnpj(null); 
        String clienteJson = objectMapper.writeValueAsString(clientePessoaJuridica);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        Cliente salvo = clienteRepository.save(clientePessoaFisica);
        
        salvo.setNome("Ana Maria Santos");
        salvo.setTelefone("(11) 97777-8888");
        salvo.setEndereco("Rua Nova, 456");

        String clienteJson = objectMapper.writeValueAsString(salvo);

        mockMvc.perform(put("/api/clientes/{id}", salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Ana Maria Santos")))
                .andExpect(jsonPath("$.telefone", is("(11) 97777-8888")))
                .andExpect(jsonPath("$.endereco", is("Rua Nova, 456")));
    }

    @Test
    void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
        String clienteJson = objectMapper.writeValueAsString(clientePessoaFisica);

        mockMvc.perform(put("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deveExcluirCliente() throws Exception {
        Cliente salvo = clienteRepository.save(clientePessoaFisica);

        mockMvc.perform(delete("/api/clientes/{id}", salvo.getId()))
                .andExpect(status().isNoContent());

        Cliente clienteExcluido = clienteRepository.findById(salvo.getId()).orElse(null);
        assert clienteExcluido != null;
        assert !clienteExcluido.getAtivo();
    }

    @Test
    void deveRetornar404AoExcluirClienteInexistente() throws Exception {
        mockMvc.perform(delete("/api/clientes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404ParaClienteInexistente() throws Exception {
        mockMvc.perform(get("/api/clientes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400ParaTipoInvalido() throws Exception {
        mockMvc.perform(get("/api/clientes/tipo/INVALIDO"))
                .andExpect(status().isBadRequest());
    }
}
