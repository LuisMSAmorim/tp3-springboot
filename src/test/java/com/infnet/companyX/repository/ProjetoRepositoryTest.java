package com.infnet.companyX.repository;

import com.infnet.companyX.entity.Cliente;
import com.infnet.companyX.entity.Funcionario;
import com.infnet.companyX.entity.Projeto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProjetoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjetoRepository projetoRepository;

    private Projeto projeto1;
    private Projeto projeto2;
    private Cliente cliente;
    private Funcionario gerente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setNome("Empresa ABC");
        cliente.setEmail("contato@abc.com");
        cliente.setTipoPessoa(Cliente.TipoPessoa.JURIDICA);
        cliente.setCnpj("12.345.678/0001-99");
        cliente.setAtivo(true);
        cliente = entityManager.persistAndFlush(cliente);

        gerente = new Funcionario();
        gerente.setNome("João Gerente");
        gerente.setEmail("joao@empresa.com");
        gerente.setCargo("Gerente");
        gerente.setDepartamento("TI");
        gerente.setAtivo(true);
        gerente = entityManager.persistAndFlush(gerente);

        projeto1 = new Projeto();
        projeto1.setNome("Sistema Web");
        projeto1.setCliente(cliente);
        projeto1.setGerente(gerente);
        projeto1.setStatus(Projeto.StatusProjeto.EM_ANDAMENTO);
        projeto1.setDataInicio(LocalDate.of(2024, 1, 1));
        projeto1.setDataFimPrevista(LocalDate.of(2024, 6, 30));
        projeto1.setOrcamento(new BigDecimal("50000.00"));
        projeto1.setPrioridade(1);
        projeto1.setAtivo(true);

        projeto2 = new Projeto();
        projeto2.setNome("App Mobile");
        projeto2.setCliente(cliente);
        projeto2.setGerente(gerente);
        projeto2.setStatus(Projeto.StatusProjeto.PLANEJAMENTO);
        projeto2.setDataInicio(LocalDate.of(2024, 3, 1));
        projeto2.setDataFimPrevista(LocalDate.of(2024, 12, 31));
        projeto2.setOrcamento(new BigDecimal("80000.00"));
        projeto2.setPrioridade(2);
        projeto2.setAtivo(true);

        entityManager.persistAndFlush(projeto1);
        entityManager.persistAndFlush(projeto2);
    }

    @Test
    void deveBuscarProjetosPorStatus() {
        List<Projeto> projetos = projetoRepository.findByStatus(Projeto.StatusProjeto.EM_ANDAMENTO);

        assertThat(projetos).hasSize(1);
        assertThat(projetos.get(0).getNome()).isEqualTo("Sistema Web");
    }

    @Test
    void deveBuscarProjetosPorCliente() {
        List<Projeto> projetos = projetoRepository.findByClienteId(cliente.getId());

        assertThat(projetos).hasSize(2);
        assertThat(projetos).extracting(Projeto::getCliente)
                .allMatch(c -> c.getId().equals(cliente.getId()));
    }

    @Test
    void deveBuscarProjetosPorGerente() {
        List<Projeto> projetos = projetoRepository.findByGerenteId(gerente.getId());

        assertThat(projetos).hasSize(2);
        assertThat(projetos).extracting(Projeto::getGerente)
                .allMatch(g -> g.getId().equals(gerente.getId()));
    }

    @Test
    void deveBuscarApenasAtivos() {
        projeto1.setAtivo(false);
        entityManager.persistAndFlush(projeto1);

        List<Projeto> projetos = projetoRepository.findByAtivoTrue();

        assertThat(projetos).hasSize(1);
        assertThat(projetos.get(0).getNome()).isEqualTo("App Mobile");
    }

    @Test
    void deveBuscarPorNomeContendo() {
        List<Projeto> projetos = projetoRepository.findByNomeContaining("Sistema");

        assertThat(projetos).hasSize(1);
        assertThat(projetos.get(0).getNome()).isEqualTo("Sistema Web");
    }

    @Test
    void deveBuscarPorPrazoEntrega() {
        List<Projeto> projetos = projetoRepository.findByPrazoEntrega(
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 7, 31)
        );

        assertThat(projetos).hasSize(1);
        assertThat(projetos.get(0).getNome()).isEqualTo("Sistema Web");
    }

    @Test
    void deveBuscarPorPrioridade() {
        List<Projeto> projetos = projetoRepository.findByPrioridade(1);

        assertThat(projetos).hasSize(1);
        assertThat(projetos.get(0).getNome()).isEqualTo("Sistema Web");
        assertThat(projetos.get(0).getPrioridade()).isEqualTo(1);
    }

    @Test
    void deveBuscarProjetosAtrasados() {
        projeto1.setDataFimPrevista(LocalDate.now().minusDays(10));
        entityManager.persistAndFlush(projeto1);

        List<Projeto> projetos = projetoRepository.findProjetosAtrasados(LocalDate.now());

        assertThat(projetos).hasSize(1);
        assertThat(projetos.get(0).getNome()).isEqualTo("Sistema Web");
    }

    @Test
    void naoDeveEncontrarProjetosAtrasadosQuandoNaoHouver() {
        entityManager.clear();
        projetoRepository.deleteAll();
        
        Projeto projetoNoFuturo = new Projeto();
        projetoNoFuturo.setNome("Projeto Futuro");
        projetoNoFuturo.setCliente(cliente);
        projetoNoFuturo.setGerente(gerente);
        projetoNoFuturo.setStatus(Projeto.StatusProjeto.EM_ANDAMENTO);
        projetoNoFuturo.setDataFimPrevista(LocalDate.now().plusDays(30));
        projetoNoFuturo.setAtivo(true);
        entityManager.persistAndFlush(projetoNoFuturo);

        List<Projeto> projetos = projetoRepository.findProjetosAtrasados(LocalDate.now());

        assertThat(projetos).isEmpty();
    }

    @Test
    void deveBuscarProjetosPorStatusOrdenadosPorPrioridade() {
        Projeto projeto3 = new Projeto();
        projeto3.setNome("Sistema ERP");
        projeto3.setCliente(cliente);
        projeto3.setGerente(gerente);
        projeto3.setStatus(Projeto.StatusProjeto.PLANEJAMENTO);
        projeto3.setPrioridade(1);
        projeto3.setAtivo(true);
        entityManager.persistAndFlush(projeto3);

        List<Projeto> projetos = projetoRepository.findByPrioridade(1);

        assertThat(projetos).hasSize(2);
        assertThat(projetos).extracting(Projeto::getPrioridade)
                .containsOnly(1);
    }
}