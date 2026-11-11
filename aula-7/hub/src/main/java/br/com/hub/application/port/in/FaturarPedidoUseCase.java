package br.com.hub.application.port.in;

import br.com.hub.domain.model.Pedido;

/** Porta de entrada: faturar um pedido e devolver o ID da fatura. */
public interface FaturarPedidoUseCase {

    String faturar(Pedido pedido);
}
