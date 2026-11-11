package br.com.hub.domain.exception;

public class FaturaNaoEncontradaException extends RuntimeException {

    public FaturaNaoEncontradaException(String faturaId) {
        super("Fatura " + faturaId + " não encontrada.");
    }
}
