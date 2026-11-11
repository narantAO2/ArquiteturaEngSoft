package br.com.mvc.service;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.integracao.HubFaturamentoClient;
import br.com.mvc.model.ItemPedido;
import br.com.mvc.model.Pedido;
import br.com.mvc.model.Perfil;
import br.com.mvc.model.Produto;
import br.com.mvc.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;
    private final HubFaturamentoClient hubFaturamento;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoService produtoService,
                         EstoqueService estoqueService,
                         HubFaturamentoClient hubFaturamento) {
        this.pedidoRepository = pedidoRepository;
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
        this.hubFaturamento = hubFaturamento;
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAllByOrderByIdDesc();
    }

    public Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Pedido " + id + " não encontrado."));
    }

    @Transactional
    public Pedido criar(Pedido pedido) {
        if (pedido.getClienteNome() == null || pedido.getClienteNome().isBlank()) {
            throw new RegraNegocioException("Informe o nome do cliente.");
        }
        if (pedido.getClienteEmail() == null || pedido.getClienteEmail().isBlank()) {
            throw new RegraNegocioException("Informe o e-mail do cliente.");
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        if (produtoId == null) {
            throw new RegraNegocioException("Selecione um produto.");
        }
        Pedido pedido = buscar(pedidoId);
        Produto produto = produtoService.buscar(produtoId);
        pedido.adicionarItem(produto, quantidade == null ? 0 : quantidade);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void removerItem(Long pedidoId, Long itemId) {
        Pedido pedido = buscar(pedidoId);
        pedido.removerItem(itemId);
        pedidoRepository.save(pedido);
    }

    /**
     * Fecha o pedido:
     *  1. dá baixa no estoque de cada item (regras da aula 6);
     *  2. envia o pedido ao hub de faturamento via REST (aula 7);
     *  3. guarda o ID da fatura devolvido e marca o pedido como FECHADO.
     *
     * Tudo na mesma transação: se o hub falhar, a baixa de estoque é desfeita
     * e o pedido continua ABERTO (ver análise em aula-8/analise-integracao.md).
     */
    @Transactional
    public Pedido fechar(Long pedidoId, Perfil perfil) {
        Pedido pedido = buscar(pedidoId);
        pedido.validarPodeFechar();

        for (ItemPedido item : pedido.getItens()) {
            estoqueService.registrarSaida(item.getProduto().getId(), item.getQuantidade(), perfil,
                    "Venda - pedido " + pedido.getId());
        }

        String faturaId = hubFaturamento.faturar(pedido);
        pedido.fechar(faturaId);
        return pedidoRepository.save(pedido);
    }
}
