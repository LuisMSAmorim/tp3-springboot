package com.infnet.companyX.repository;

import com.infnet.companyX.entity.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProdutoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    void setUp() {
        produto1 = new Produto();
        produto1.setNome("Notebook Dell");
        produto1.setCodigo("DELL001");
        produto1.setPreco(new BigDecimal("3500.00"));
        produto1.setQuantidadeEstoque(10);
        produto1.setCategoria("Informática");
        produto1.setMarca("Dell");
        produto1.setAtivo(true);

        produto2 = new Produto();
        produto2.setNome("Mouse Logitech");
        produto2.setCodigo("LOG001");
        produto2.setPreco(new BigDecimal("150.00"));
        produto2.setQuantidadeEstoque(5);
        produto2.setCategoria("Informática");
        produto2.setMarca("Logitech");
        produto2.setAtivo(true);

        entityManager.persistAndFlush(produto1);
        entityManager.persistAndFlush(produto2);
    }

    @Test
    void deveBuscarProdutoPorCodigo() {
        Optional<Produto> encontrado = produtoRepository.findByCodigo("DELL001");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Notebook Dell");
    }

    @Test
    void deveBuscarProdutosPorCategoria() {
        List<Produto> produtos = produtoRepository.findByCategoria("Informática");

        assertThat(produtos).hasSize(2);
        assertThat(produtos).extracting(Produto::getCategoria)
                .containsOnly("Informática");
    }

    @Test
    void deveBuscarProdutosPorMarca() {
        List<Produto> produtos = produtoRepository.findByMarca("Dell");

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getNome()).isEqualTo("Notebook Dell");
    }

    @Test
    void deveBuscarApenasAtivos() {
        produto1.setAtivo(false);
        entityManager.persistAndFlush(produto1);

        List<Produto> produtos = produtoRepository.findByAtivoTrue();

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getNome()).isEqualTo("Mouse Logitech");
    }

    @Test
    void deveBuscarPorNomeContendo() {
        List<Produto> produtos = produtoRepository.findByNomeContaining("Dell");

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getNome()).isEqualTo("Notebook Dell");
    }

    @Test
    void deveBuscarPorFaixaPreco() {
        List<Produto> produtos = produtoRepository.findByPrecoRange(
                new BigDecimal("100.00"), 
                new BigDecimal("200.00")
        );

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getNome()).isEqualTo("Mouse Logitech");
    }

    @Test
    void deveBuscarEstoqueBaixo() {
        List<Produto> produtos = produtoRepository.findEstoqueBaixo(8);

        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getNome()).isEqualTo("Mouse Logitech");
        assertThat(produtos.get(0).getQuantidadeEstoque()).isEqualTo(5);
    }

    @Test
    void naoDeveEncontrarProdutoComCodigoInexistente() {
        Optional<Produto> encontrado = produtoRepository.findByCodigo("INEXISTENTE");

        assertThat(encontrado).isEmpty();
    }

    @Test
    void deveBuscarProdutosComEstoqueBaixo() {
        produto1.setQuantidadeEstoque(2);
        entityManager.persistAndFlush(produto1);

        List<Produto> produtos = produtoRepository.findEstoqueBaixo(10);

        assertThat(produtos).hasSize(2);
        assertThat(produtos).extracting(Produto::getQuantidadeEstoque)
                .allMatch(estoque -> estoque < 10);
    }
}

