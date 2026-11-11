package br.com.mvc.integracao;

import java.math.BigDecimal;
import java.util.List;

/** Corpo do POST /faturas no hub (mesmo contrato do adaptador REST do hub). */
public record FaturamentoRequest(String pedidoId, Cliente cliente, List<Item> itens) {

    public record Cliente(String nome, String email, String documento) {
    }

    public record Item(String descricao, Integer quantidade, BigDecimal valorUnitario) {
    }
}
