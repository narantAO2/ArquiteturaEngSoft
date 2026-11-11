package br.com.mvc.service;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.MovimentacaoEstoque;
import br.com.mvc.model.Perfil;
import br.com.mvc.model.Produto;
import br.com.mvc.model.StatusProduto;
import br.com.mvc.model.TipoMovimentacao;
import br.com.mvc.repository.MovimentacaoEstoqueRepository;
import br.com.mvc.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public EstoqueService(ProdutoRepository produtoRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /** Produtos que podem ser movimentados (todos, exceto descontinuados). */
    public List<Produto> produtosMovimentaveis() {
        return produtoRepository.findByStatusNotOrderByNomeAsc(StatusProduto.DESCONTINUADO);
    }

    public List<MovimentacaoEstoque> historico() {
        return movimentacaoRepository.findTop50ByOrderByDataHoraDesc();
    }

    @Transactional
    public MovimentacaoEstoque registrarEntrada(Long produtoId, int quantidade, Perfil perfil, String observacao) {
        Produto produto = buscarProduto(produtoId);
        produto.registrarEntrada(quantidade);          // INDISPONIVEL -> DISPONIVEL quando sai de zero
        produtoRepository.save(produto);
        return movimentacaoRepository.save(
                new MovimentacaoEstoque(produto, TipoMovimentacao.ENTRADA, quantidade, perfil, observacao));
    }

    @Transactional
    public MovimentacaoEstoque registrarSaida(Long produtoId, int quantidade, Perfil perfil, String observacao) {
        Produto produto = buscarProduto(produtoId);
        produto.registrarSaida(quantidade);            // DISPONIVEL -> INDISPONIVEL quando zera
        produtoRepository.save(produto);
        return movimentacaoRepository.save(
                new MovimentacaoEstoque(produto, TipoMovimentacao.SAIDA, quantidade, perfil, observacao));
    }

    @Transactional
    public MovimentacaoEstoque movimentar(Long produtoId, TipoMovimentacao tipo, Integer quantidade,
                                          Perfil perfil, String observacao) {
        if (produtoId == null) {
            throw new RegraNegocioException("Selecione um produto.");
        }
        if (tipo == null) {
            throw new RegraNegocioException("Selecione o tipo de movimentação.");
        }
        int qtd = quantidade == null ? 0 : quantidade;
        return tipo == TipoMovimentacao.ENTRADA
                ? registrarEntrada(produtoId, qtd, perfil, observacao)
                : registrarSaida(produtoId, qtd, perfil, observacao);
    }

    private Produto buscarProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Produto " + id + " não encontrado."));
    }
}
