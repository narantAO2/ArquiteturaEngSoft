package br.com.mvc.model;

import br.com.mvc.exception.RegraNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clienteNome;

    @Column(nullable = false)
    private String clienteEmail;

    private String clienteDocumento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.ABERTO;

    /** ID da fatura devolvido pelo hub de faturamento ao fechar o pedido. */
    private String faturaId;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    private LocalDateTime fechadoEm;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    public void adicionarItem(Produto produto, int quantidade) {
        validarAberto();
        if (produto.getStatus() != StatusProduto.DISPONIVEL) {
            throw new RegraNegocioException("O produto \"" + produto.getNome() + "\" não está disponível para venda.");
        }
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
        int jaNoPedido = itens.stream()
                .filter(i -> i.getProduto().getId().equals(produto.getId()))
                .mapToInt(ItemPedido::getQuantidade).sum();
        if (jaNoPedido + quantidade > produto.getQuantidade()) {
            throw new RegraNegocioException("Estoque insuficiente para \"" + produto.getNome() + "\". Saldo: "
                    + produto.getQuantidade() + ", já no pedido: " + jaNoPedido + ".");
        }
        itens.add(new ItemPedido(this, produto, quantidade));
    }

    public void removerItem(Long itemId) {
        validarAberto();
        itens.removeIf(i -> i.getId() != null && i.getId().equals(itemId));
    }

    /** Fecha o pedido guardando o ID da fatura criada pelo hub. */
    public void fechar(String faturaId) {
        validarAberto();
        if (faturaId == null || faturaId.isBlank()) {
            throw new RegraNegocioException("O hub não devolveu o ID da fatura.");
        }
        this.faturaId = faturaId;
        this.status = StatusPedido.FECHADO;
        this.fechadoEm = LocalDateTime.now();
    }

    public void validarPodeFechar() {
        validarAberto();
        if (itens.isEmpty()) {
            throw new RegraNegocioException("Adicione pelo menos um item antes de fechar o pedido.");
        }
    }

    private void validarAberto() {
        if (status != StatusPedido.ABERTO) {
            throw new RegraNegocioException("O pedido " + id + " já está fechado.");
        }
    }

    public boolean isAberto() {
        return status == StatusPedido.ABERTO;
    }

    public BigDecimal getValorTotal() {
        return itens.stream().map(ItemPedido::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public String getClienteDocumento() {
        return clienteDocumento;
    }

    public void setClienteDocumento(String clienteDocumento) {
        this.clienteDocumento = clienteDocumento;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public String getFaturaId() {
        return faturaId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getFechadoEm() {
        return fechadoEm;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }
}
