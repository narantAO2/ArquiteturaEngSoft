package br.com.mvc.model;

/**
 * Perfis de usuário do Sistema de Vendas (ver diagramas C4 da aula 4).
 */
public enum Perfil {
    ADMINISTRADOR("Administrador"),
    VENDEDOR("Vendedor"),
    ESTOQUE("Equipe de Estoque");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
