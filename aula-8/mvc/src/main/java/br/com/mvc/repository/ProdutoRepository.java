package br.com.mvc.repository;

import br.com.mvc.model.Produto;
import br.com.mvc.model.StatusProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByOrderByNomeAsc();

    List<Produto> findByStatusNotOrderByNomeAsc(StatusProduto status);
}
