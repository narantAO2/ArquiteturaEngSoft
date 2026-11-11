package br.com.mvc.service;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.Perfil;
import br.com.mvc.model.Produto;
import br.com.mvc.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listar() {
        return produtoRepository.findAllByOrderByNomeAsc();
    }

    public Produto buscar(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Produto " + id + " não encontrado."));
    }

    /** Cadastro: o produto sempre nasce INDISPONÍVEL e com estoque zero. */
    @Transactional
    public Produto cadastrar(Produto produto) {
        validar(produto);
        produto.inicializar();
        return produtoRepository.save(produto);
    }

    /** Edição: altera apenas os dados cadastrais; status e saldo são controlados pelo estoque. */
    @Transactional
    public Produto atualizar(Long id, Produto dados) {
        validar(dados);
        Produto produto = buscar(id);
        produto.setNome(dados.getNome());
        produto.setDescricao(dados.getDescricao());
        produto.setPreco(dados.getPreco());
        return produtoRepository.save(produto);
    }

    @Transactional
    public void descontinuar(Long id, Perfil perfil) {
        Produto produto = buscar(id);
        produto.descontinuar(perfil);
        produtoRepository.save(produto);
    }

    private void validar(Produto produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new RegraNegocioException("Informe o nome do produto.");
        }
        if (produto.getPreco() == null || produto.getPreco().signum() < 0) {
            throw new RegraNegocioException("Informe um preço válido.");
        }
    }
}
