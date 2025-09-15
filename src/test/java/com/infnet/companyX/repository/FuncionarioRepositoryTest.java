package com.infnet.companyX.repository;

import com.infnet.companyX.entity.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FuncionarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    private Funcionario funcionario1;
    private Funcionario funcionario2;

    @BeforeEach
    void setUp() {
        funcionario1 = new Funcionario();
        funcionario1.setNome("João Silva");
        funcionario1.setEmail("joao@empresa.com");
        funcionario1.setCargo("Desenvolvedor");
        funcionario1.setDepartamento("TI");
        funcionario1.setSalario(new BigDecimal("5000.00"));
        funcionario1.setDataAdmissao(LocalDate.of(2024, 1, 15));
        funcionario1.setCpf("123.456.789-01");
        funcionario1.setAtivo(true);

        funcionario2 = new Funcionario();
        funcionario2.setNome("Maria Santos");
        funcionario2.setEmail("maria@empresa.com");
        funcionario2.setCargo("Analista");
        funcionario2.setDepartamento("TI");
        funcionario2.setSalario(new BigDecimal("4500.00"));
        funcionario2.setCpf("987.654.321-00");
        funcionario2.setAtivo(true);

        entityManager.persistAndFlush(funcionario1);
        entityManager.persistAndFlush(funcionario2);
    }

    @Test
    void deveBuscarFuncionarioPorEmail() {
        Optional<Funcionario> encontrado = funcionarioRepository.findByEmail("joao@empresa.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveBuscarFuncionarioPorCpf() {
        Optional<Funcionario> encontrado = funcionarioRepository.findByCpf("123.456.789-01");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveBuscarFuncionariosPorDepartamento() {
        List<Funcionario> funcionarios = funcionarioRepository.findByDepartamento("TI");

        assertThat(funcionarios).hasSize(2);
        assertThat(funcionarios).extracting(Funcionario::getDepartamento)
                .containsOnly("TI");
    }

    @Test
    void deveBuscarFuncionariosPorCargo() {
        List<Funcionario> funcionarios = funcionarioRepository.findByCargo("Desenvolvedor");

        assertThat(funcionarios).hasSize(1);
        assertThat(funcionarios.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveBuscarApenasAtivos() {
        funcionario1.setAtivo(false);
        entityManager.persistAndFlush(funcionario1);

        List<Funcionario> funcionarios = funcionarioRepository.findByAtivoTrue();

        assertThat(funcionarios).hasSize(1);
        assertThat(funcionarios.get(0).getNome()).isEqualTo("Maria Santos");
    }

    @Test
    void deveBuscarPorNomeContendo() {
        List<Funcionario> funcionarios = funcionarioRepository.findByNomeContaining("João");

        assertThat(funcionarios).hasSize(1);
        assertThat(funcionarios.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    void naoDeveEncontrarFuncionarioComEmailInexistente() {
        Optional<Funcionario> encontrado = funcionarioRepository.findByEmail("inexistente@empresa.com");

        assertThat(encontrado).isEmpty();
    }

    @Test
    void naoDeveEncontrarFuncionarioComCpfInexistente() {
        Optional<Funcionario> encontrado = funcionarioRepository.findByCpf("000.000.000-00");

        assertThat(encontrado).isEmpty();
    }
}

