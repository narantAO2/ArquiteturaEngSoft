package br.com.hub.application.service;

import br.com.hub.application.port.in.ConsultarFaturaUseCase;
import br.com.hub.application.port.in.FaturarPedidoUseCase;
import br.com.hub.application.port.out.EmissaoBoletoPort;
import br.com.hub.application.port.out.EmissaoNotaFiscalPort;
import br.com.hub.application.port.out.FaturaRepositoryPort;
import br.com.hub.application.port.out.NotificacaoClientePort;
import br.com.hub.domain.exception.FaturaNaoEncontradaException;
import br.com.hub.domain.model.Fatura;
import br.com.hub.domain.model.Pedido;

import java.util.List;

/**
 * Núcleo da aplicação (dentro do hexágono). Só conhece as portas,
 * nunca os adaptadores (fila, SOAP, SMTP, banco).
 */
public class FaturamentoService implements FaturarPedidoUseCase, ConsultarFaturaUseCase {

    private final FaturaRepositoryPort faturaRepository;
    private final EmissaoBoletoPort emissaoBoleto;
    private final EmissaoNotaFiscalPort emissaoNotaFiscal;
    private final NotificacaoClientePort notificacaoCliente;

    public FaturamentoService(FaturaRepositoryPort faturaRepository,
                              EmissaoBoletoPort emissaoBoleto,
                              EmissaoNotaFiscalPort emissaoNotaFiscal,
                              NotificacaoClientePort notificacaoCliente) {
        this.faturaRepository = faturaRepository;
        this.emissaoBoleto = emissaoBoleto;
        this.emissaoNotaFiscal = emissaoNotaFiscal;
        this.notificacaoCliente = notificacaoCliente;
    }

    @Override
    public String faturar(Pedido pedido) {
        // RF01 – cria e persiste a fatura
        Fatura fatura = faturaRepository.salvar(Fatura.criar(pedido));

        // RF02 – emite o boleto (fila) e guarda o código de barras
        fatura.registrarBoleto(emissaoBoleto.emitir(fatura));

        // RF03 – emite a nota fiscal (SOAP) e guarda a chave de acesso
        fatura.registrarNotaFiscal(emissaoNotaFiscal.emitir(fatura));

        // RF04 – notifica o cliente por e-mail (SMTP) e guarda o hash do conteúdo
        fatura.registrarEmail(notificacaoCliente.notificar(fatura));

        fatura.concluir();
        faturaRepository.salvar(fatura);

        // RF01 – devolve o ID para a camada REST
        return fatura.getId();
    }

    @Override
    public Fatura buscar(String faturaId) {
        return faturaRepository.buscarPorId(faturaId)
                .orElseThrow(() -> new FaturaNaoEncontradaException(faturaId));
    }

    @Override
    public List<Fatura> listar() {
        return faturaRepository.listar();
    }
}
