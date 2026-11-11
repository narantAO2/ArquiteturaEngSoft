package br.com.hub.domain.model;

import br.com.hub.domain.exception.RegraFaturamentoException;

import java.math.BigDecimal;

public record ItemPedido(String descricao, int quantidade, BigDecimal valorUnitario) {

    public ItemPedido {
        if (descricao == null || descricao.isBlank()) {
            throw new RegraFaturamentoException("A descrição do item é obrigatória.");
        }
        if (quantidade <= 0) {
            throw new RegraFaturamentoException("A quantidade do item deve ser maior que zero.");
        }
        if (valorUnitario == null || valorUnitario.signum() < 0) {
            throw new RegraFaturamentoException("O valor unitário do item é inválido.");
        }
    }

    public BigDecimal subtotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
