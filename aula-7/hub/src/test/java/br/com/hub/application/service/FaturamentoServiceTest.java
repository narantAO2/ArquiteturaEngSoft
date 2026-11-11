package br.com.hub.application.service;

import br.com.hub.adapter.out.boleto.BoletoFilaAdapter;
import br.com.hub.adapter.out.email.EmailSmtpAdapter;
import br.com.hub.adapter.out.notafiscal.NotaFiscalSoapAdapter;
import br.com.hub.adapter.out.persistence.FaturaRepositoryEmMemoriaAdapter;
import br.com.hub.domain.exception.RegraFaturamentoException;
import br.com.hub.domain.model.Cliente;
import br.com.hub.domain.model.Fatura;
import br.com.hub.domain.model.ItemPedido;
import br.com.hub.domain.model.Pedido;
import br.com.hub.domain.model.StatusFatura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaturamentoServiceTest {

    private FaturamentoService service;

    @BeforeEach
    void setUp() {
        service = new FaturamentoService(
                new FaturaRepositoryEmMemoriaAdapter(),
                new BoletoFilaAdapter(),
                new NotaFiscalSoapAdapter(),
                new EmailSmtpAdapter());
    }

    private Pedido pedido() {
        return new Pedido("PED-1",
                new Cliente("Maria", "maria@email.com", "12345678909"),
                List.of(new ItemPedido("Caneca", 2, new BigDecimal("29.90")),
                        new ItemPedido("Camiseta", 1, new BigDecimal("59.90"))));
    }

    @Test
    void rf01_criaPersisteEDevolveOId() {
        String id = service.faturar(pedido());
        assertNotNull(id);
        Fatura fatura = service.buscar(id);
        assertEquals("PED-1", fatura.getPedido().pedidoId());
        assertEquals(new BigDecimal("119.70"), fatura.getValorTotal());
        assertEquals(StatusFatura.FATURADA, fatura.getStatus());
    }

    @Test
    void rf02_boletoGeraCodigoDeBarrasGuardadoNaFatura() {
        Fatura fatura = service.buscar(service.faturar(pedido()));
        assertTrue(fatura.getCodigoBarrasBoleto().matches("\\d{44}"));
    }

    @Test
    void rf03_notaGeraChaveDeAcessoDe44Digitos() {
        Fatura fatura = service.buscar(service.faturar(pedido()));
        assertTrue(fatura.getChaveAcessoNotaFiscal().matches("\\d{44}"));
    }

    @Test
    void rf04_emailGeraHashDoConteudo() {
        Fatura fatura = service.buscar(service.faturar(pedido()));
        assertTrue(fatura.getHashEmail().matches("[0-9a-f]{64}"));
    }

    @Test
    void pedidoSemItensNaoEFaturado() {
        assertThrows(RegraFaturamentoException.class, () -> new Pedido("PED-2",
                new Cliente("Maria", "maria@email.com", "12345678909"), List.of()));
    }

    @Test
    void faturaRejeitaComprovantesInvalidos() {
        Fatura fatura = Fatura.criar(pedido());
        assertThrows(RegraFaturamentoException.class, () -> fatura.registrarBoleto("123"));
        assertThrows(RegraFaturamentoException.class, () -> fatura.registrarNotaFiscal("abc"));
        assertThrows(RegraFaturamentoException.class, () -> fatura.registrarEmail("xyz"));
        assertThrows(RegraFaturamentoException.class, fatura::concluir);
    }
}
