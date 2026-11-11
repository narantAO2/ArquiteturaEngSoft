package br.com.mvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

@Entity
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    /** Preço do produto no momento da venda. */
    @Column(nullable = false)
    private BigDecimal valorUnitario;

    public ItemPedido() {
    }

    public ItemPedido(Pedido pedido, Produto produto, Integer quantidade) {
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.valorUnitario = produto.getPreco();
    }

    public BigDecimal getSubtotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }
}
