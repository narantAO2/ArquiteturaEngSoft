package br.com.mvc.exception;

/**
 * Lançada quando uma regra de negócio é violada (ex.: saída sem saldo).
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
