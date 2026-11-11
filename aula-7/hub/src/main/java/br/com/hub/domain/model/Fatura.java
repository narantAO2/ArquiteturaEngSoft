package br.com.hub.domain.model;

import br.com.hub.domain.exception.RegraFaturamentoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade central do hub. Não depende de framework nenhum.
 * As restrições dos requisitos ficam aqui:
 *  - RF02: código de barras do boleto (44 dígitos) guardado na fatura;
 *  - RF03: chave de acesso da NF-e (44 dígitos) guardada na fatura;
 *  - RF04: hash SHA-256 do conteúdo do e-mail guardado na fatura.
 */
public class Fatura {

    private static final String QUARENTA_E_QUATRO_DIGITOS = "\\d{44}";
    private static final String HASH_SHA256 = "[0-9a-f]{64}";

    private final String id;
    private final Pedido pedido;
    private final BigDecimal valorTotal;
    private final LocalDateTime criadaEm;
    private StatusFatura status;
    private String codigoBarrasBoleto;
    private String chaveAcessoNotaFiscal;
    private String hashEmail;

    private Fatura(String id, Pedido pedido) {
        this.id = id;
        this.pedido = pedido;
        this.valorTotal = pedido.valorTotal();
        this.criadaEm = LocalDateTime.now();
        this.status = StatusFatura.CRIADA;
    }

    /** RF01 – cria a fatura a partir do pedido recebido. */
    public static Fatura criar(Pedido pedido) {
        if (pedido == null) {
            throw new RegraFaturamentoException("O pedido é obrigatório.");
        }
        return new Fatura(UUID.randomUUID().toString(), pedido);
    }

    /** RF02 – restrição: o boleto emitido gera um código de barras guardado na fatura. */
    public void registrarBoleto(String codigoBarras) {
        if (codigoBarras == null || !codigoBarras.matches(QUARENTA_E_QUATRO_DIGITOS)) {
            throw new RegraFaturamentoException("Código de barras do boleto inválido: deve ter 44 dígitos.");
        }
        this.codigoBarrasBoleto = codigoBarras;
    }

    /** RF03 – restrição: a nota emitida gera uma chave de acesso de 44 dígitos guardada na fatura. */
    public void registrarNotaFiscal(String chaveAcesso) {
        if (chaveAcesso == null || !chaveAcesso.matches(QUARENTA_E_QUATRO_DIGITOS)) {
            throw new RegraFaturamentoException("Chave de acesso da nota fiscal inválida: deve ter 44 dígitos.");
        }
        this.chaveAcessoNotaFiscal = chaveAcesso;
    }

    /** RF04 – restrição: o e-mail enviado gera um hash do conteúdo guardado na fatura. */
    public void registrarEmail(String hashConteudo) {
        if (hashConteudo == null || !hashConteudo.matches(HASH_SHA256)) {
            throw new RegraFaturamentoException("Hash do e-mail inválido: esperado SHA-256 em hexadecimal.");
        }
        this.hashEmail = hashConteudo;
    }

    /** Só fica FATURADA quando as três integrações deixaram seus comprovantes na fatura. */
    public void concluir() {
        if (codigoBarrasBoleto == null || chaveAcessoNotaFiscal == null || hashEmail == null) {
            throw new RegraFaturamentoException("A fatura só pode ser concluída após boleto, nota fiscal e e-mail.");
        }
        this.status = StatusFatura.FATURADA;
    }

    public String getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public StatusFatura getStatus() {
        return status;
    }

    public String getCodigoBarrasBoleto() {
        return codigoBarrasBoleto;
    }

    public String getChaveAcessoNotaFiscal() {
        return chaveAcessoNotaFiscal;
    }

    public String getHashEmail() {
        return hashEmail;
    }
}
