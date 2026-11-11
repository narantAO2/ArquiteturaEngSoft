package br.com.hub.application.port.out;

import br.com.hub.domain.model.Fatura;

/** Porta de saída: emissão do boleto (adaptador usa FILA). Devolve o código de barras. */
public interface EmissaoBoletoPort {

    String emitir(Fatura fatura);
}
