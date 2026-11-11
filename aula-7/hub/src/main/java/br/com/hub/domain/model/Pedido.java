package br.com.hub.domain.model;

import br.com.hub.domain.exception.RegraFaturamentoException;

import java.math.BigDecimal;
import java.util.List;

/** Pedido recebido da aplicação de vendas para ser faturado. */
public record Pedido(String pedidoId, Cliente cliente, List<ItemPedido> itens) {

    public Pedido {
        if (pedidoId == null || pedidoId.isBlank()) {
            throw new RegraFaturamentoException("O identificador do pedido é obrigatório.");
        }
        if (cliente == null) {
            throw new RegraFaturamentoException("O cliente do pedido é obrigatório.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new RegraFaturamentoException("O pedido deve ter pelo menos um item.");
        }
        itens = List.copyOf(itens);
    }

    public BigDecimal valorTotal() {
        return itens.stream().map(ItemPedido::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
