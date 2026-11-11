package br.com.hub.application.port.out;

import br.com.hub.domain.model.Fatura;

/** Porta de saída: emissão da nota fiscal (adaptador usa SOAP). Devolve a chave de acesso. */
public interface EmissaoNotaFiscalPort {

    String emitir(Fatura fatura);
}
