package br.com.hub.domain.exception;

/** Violação de uma regra de negócio/restrição do faturamento. */
public class RegraFaturamentoException extends RuntimeException {

    public RegraFaturamentoException(String mensagem) {
        super(mensagem);
    }
}
