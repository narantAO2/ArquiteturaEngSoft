package br.com.hub.adapter.out.email;

import br.com.hub.application.port.out.NotificacaoClientePort;
import br.com.hub.domain.model.Fatura;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Adaptador de saída SIMULADO: notificação do cliente por e-mail via SMTP.
 *
 * Monta a mensagem, registra no log o diálogo SMTP que seria feito com o servidor
 * e devolve o hash SHA-256 do conteúdo enviado.
 */
public class EmailSmtpAdapter implements NotificacaoClientePort {

    private static final System.Logger LOG = System.getLogger(EmailSmtpAdapter.class.getName());
    private static final String SERVIDOR = "smtp.simulado.local:587";
    private static final String REMETENTE = "faturamento@sistemavendas.com.br";

    @Override
    public String notificar(Fatura fatura) {
        String conteudo = montarMensagem(fatura);
        LOG.log(System.Logger.Level.INFO, """
                [SMTP {0}]
                EHLO hub-faturamento
                MAIL FROM:<{1}>
                RCPT TO:<{2}>
                DATA
                {3}
                .
                250 OK: mensagem enfileirada
                QUIT""", SERVIDOR, REMETENTE, fatura.getPedido().cliente().email(), conteudo);
        return sha256(conteudo);
    }

    private String montarMensagem(Fatura fatura) {
        return """
                From: %s
                To: %s
                Subject: Pedido %s faturado

                Olá, %s!

                Seu pedido %s foi faturado.
                Valor total: R$ %s
                Código de barras do boleto: %s
                Chave de acesso da NF-e: %s
                Fatura: %s
                """.formatted(
                REMETENTE,
                fatura.getPedido().cliente().email(),
                fatura.getPedido().pedidoId(),
                fatura.getPedido().cliente().nome(),
                fatura.getPedido().pedidoId(),
                fatura.getValorTotal(),
                fatura.getCodigoBarrasBoleto(),
                fatura.getChaveAcessoNotaFiscal(),
                fatura.getId());
    }

    static String sha256(String conteudo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(conteudo.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
