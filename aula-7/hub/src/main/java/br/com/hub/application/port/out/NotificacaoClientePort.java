package br.com.hub.application.port.out;

import br.com.hub.domain.model.Fatura;

/** Porta de saída: notificação do cliente (adaptador usa SMTP). Devolve o hash do conteúdo enviado. */
public interface NotificacaoClientePort {

    String notificar(Fatura fatura);
}
