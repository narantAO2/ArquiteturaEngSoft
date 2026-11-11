package br.com.mvc.integracao;

/** Falha ao chamar o hub de faturamento (fora do ar, timeout ou erro HTTP). */
public class IntegracaoHubException extends RuntimeException {

    public IntegracaoHubException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
