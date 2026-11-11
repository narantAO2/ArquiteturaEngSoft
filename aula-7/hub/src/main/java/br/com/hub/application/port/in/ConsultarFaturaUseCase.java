package br.com.hub.application.port.in;

import br.com.hub.domain.model.Fatura;

import java.util.List;

/** Porta de entrada: consultar faturas. */
public interface ConsultarFaturaUseCase {

    Fatura buscar(String faturaId);

    List<Fatura> listar();
}
