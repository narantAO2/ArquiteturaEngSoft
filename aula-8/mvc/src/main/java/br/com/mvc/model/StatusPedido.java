package br.com.mvc.model;

public enum StatusPedido {
    ABERTO("Aberto"),
    FECHADO("Fechado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
