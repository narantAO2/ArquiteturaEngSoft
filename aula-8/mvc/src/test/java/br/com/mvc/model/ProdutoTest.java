package br.com.mvc.model;

import br.com.mvc.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoTest {

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto("Caneca", "Caneca de porcelana", new BigDecimal("29.90"));
        produto.inicializar();
    }

    @Test
    void produtoNovoComecaIndisponivelComEstoqueZero() {
        assertEquals(StatusProduto.INDISPONIVEL, produto.getStatus());
        assertEquals(0, produto.getQuantidade());
    }

    @Test
    void entradaQuandoEstoqueSaiDeZeroDeixaDisponivel() {
        produto.registrarEntrada(10);
        assertEquals(StatusProduto.DISPONIVEL, produto.getStatus());
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void saidaQueZeraEstoqueDeixaIndisponivel() {
        produto.registrarEntrada(5);
        produto.registrarSaida(3);
        assertEquals(StatusProduto.DISPONIVEL, produto.getStatus());
        produto.registrarSaida(2);
        assertEquals(StatusProduto.INDISPONIVEL, produto.getStatus());
        assertEquals(0, produto.getQuantidade());
    }

    @Test
    void naoPermiteSaidaSemEstoque() {
        assertThrows(RegraNegocioException.class, () -> produto.registrarSaida(1));
    }

    @Test
    void naoPermiteSaidaMaiorQueOSaldo() {
        produto.registrarEntrada(2);
        assertThrows(RegraNegocioException.class, () -> produto.registrarSaida(3));
        assertEquals(2, produto.getQuantidade());
    }

    @Test
    void naoPermiteQuantidadeZeroOuNegativa() {
        assertThrows(RegraNegocioException.class, () -> produto.registrarEntrada(0));
        assertThrows(RegraNegocioException.class, () -> produto.registrarEntrada(-1));
    }

    @Test
    void somenteAdministradorPodeDescontinuar() {
        assertThrows(RegraNegocioException.class, () -> produto.descontinuar(Perfil.VENDEDOR));
        assertThrows(RegraNegocioException.class, () -> produto.descontinuar(Perfil.ESTOQUE));
        produto.descontinuar(Perfil.ADMINISTRADOR);
        assertEquals(StatusProduto.DESCONTINUADO, produto.getStatus());
    }

    @Test
    void produtoDescontinuadoNaoPodeSerMovimentado() {
        produto.registrarEntrada(5);
        produto.descontinuar(Perfil.ADMINISTRADOR);
        assertThrows(RegraNegocioException.class, () -> produto.registrarEntrada(1));
        assertThrows(RegraNegocioException.class, () -> produto.registrarSaida(1));
        assertEquals(StatusProduto.DESCONTINUADO, produto.getStatus());
    }
}
