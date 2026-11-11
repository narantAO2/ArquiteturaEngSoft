package br.com.hub.adapter.in.rest;

import br.com.hub.domain.model.Fatura;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FaturaResponse(String id,
                             String pedidoId,
                             String cliente,
                             BigDecimal valorTotal,
                             String status,
                             String codigoBarrasBoleto,
                             String chaveAcessoNotaFiscal,
                             String hashEmail,
                             LocalDateTime criadaEm) {

    public static FaturaResponse de(Fatura f) {
        return new FaturaResponse(f.getId(), f.getPedido().pedidoId(), f.getPedido().cliente().nome(),
                f.getValorTotal(), f.getStatus().name(), f.getCodigoBarrasBoleto(),
                f.getChaveAcessoNotaFiscal(), f.getHashEmail(), f.getCriadaEm());
    }
}
