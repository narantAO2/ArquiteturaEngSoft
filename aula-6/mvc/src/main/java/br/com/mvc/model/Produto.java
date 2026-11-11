package br.com.mvc.model;

import br.com.mvc.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer quantidade = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProduto status = StatusProduto.INDISPONIVEL;

    public Produto() {
    }

    public Produto(String nome, String descricao, BigDecimal preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    // ---------------------------------------------------------------
    // Regras de negócio do produto/estoque
    // ---------------------------------------------------------------

    /** Todo produto novo começa INDISPONÍVEL e com estoque zerado. */
    public void inicializar() {
        this.quantidade = 0;
        this.status = StatusProduto.INDISPONIVEL;
    }

    /** Entrada de estoque: se o saldo sair de zero, o produto fica DISPONÍVEL. */
    public void registrarEntrada(int qtd) {
        validarMovimentacao(qtd);
        int saldoAnterior = this.quantidade;
        this.quantidade = saldoAnterior + qtd;
        if (saldoAnterior == 0 && this.quantidade > 0) {
            this.status = StatusProduto.DISPONIVEL;
        }
    }

    /** Saída de estoque: se o saldo zerar, o produto fica INDISPONÍVEL. */
    public void registrarSaida(int qtd) {
        validarMovimentacao(qtd);
        if (this.quantidade == 0) {
            throw new RegraNegocioException("O produto \"" + nome + "\" não possui estoque disponível.");
        }
        if (qtd > this.quantidade) {
            throw new RegraNegocioException("Estoque insuficiente para \"" + nome + "\". Saldo atual: "
                    + this.quantidade + ", solicitado: " + qtd + ".");
        }
        this.quantidade = this.quantidade - qtd;
        if (this.quantidade == 0) {
            this.status = StatusProduto.INDISPONIVEL;
        }
    }

    /** Apenas o ADMINISTRADOR pode descontinuar um produto. */
    public void descontinuar(Perfil perfil) {
        if (perfil != Perfil.ADMINISTRADOR) {
            throw new RegraNegocioException("Somente o administrador pode descontinuar um produto.");
        }
        if (isDescontinuado()) {
            throw new RegraNegocioException("O produto \"" + nome + "\" já está descontinuado.");
        }
        this.status = StatusProduto.DESCONTINUADO;
    }

    public boolean isDescontinuado() {
        return this.status == StatusProduto.DESCONTINUADO;
    }

    private void validarMovimentacao(int qtd) {
        if (isDescontinuado()) {
            throw new RegraNegocioException("O produto \"" + nome + "\" está descontinuado e não pode ser movimentado no estoque.");
        }
        if (qtd <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
    }

    // ---------------------------------------------------------------
    // Getters e setters
    // ---------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public StatusProduto getStatus() {
        return status;
    }
}
