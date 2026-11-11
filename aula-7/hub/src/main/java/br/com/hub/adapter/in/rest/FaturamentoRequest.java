package br.com.hub.adapter.in.rest;

import java.math.BigDecimal;
import java.util.List;

/** Contrato REST de entrada: o pedido enviado pela aplicação de vendas. */
public record FaturamentoRequest(String pedidoId, ClienteRequest cliente, List<ItemRequest> itens) {

    public record ClienteRequest(String nome, String email, String documento) {
    }

    public record ItemRequest(String descricao, Integer quantidade, BigDecimal valorUnitario) {
    }
}
