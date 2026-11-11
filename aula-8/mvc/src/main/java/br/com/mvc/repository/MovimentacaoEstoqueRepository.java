package br.com.mvc.repository;

import br.com.mvc.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findTop50ByOrderByDataHoraDesc();
}
