package br.com.mvc.model;

public enum TipoMovimentacao {
    ENTRADA("Entrada"),
    SAIDA("Saída");

    private final String descricao;

    TipoMovimentacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
