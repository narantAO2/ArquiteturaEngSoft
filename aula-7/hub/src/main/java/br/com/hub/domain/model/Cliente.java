package br.com.hub.domain.model;

import br.com.hub.domain.exception.RegraFaturamentoException;

public record Cliente(String nome, String email, String documento) {

    public Cliente {
        if (nome == null || nome.isBlank()) {
            throw new RegraFaturamentoException("O nome do cliente é obrigatório.");
        }
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new RegraFaturamentoException("E-mail do cliente inválido.");
        }
    }
}
