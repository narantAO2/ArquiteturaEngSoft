package br.com.hub.application.port.out;

import br.com.hub.domain.model.Fatura;

import java.util.List;
import java.util.Optional;

/** Porta de saída: persistência da fatura. */
public interface FaturaRepositoryPort {

    Fatura salvar(Fatura fatura);

    Optional<Fatura> buscarPorId(String id);

    List<Fatura> listar();
}
