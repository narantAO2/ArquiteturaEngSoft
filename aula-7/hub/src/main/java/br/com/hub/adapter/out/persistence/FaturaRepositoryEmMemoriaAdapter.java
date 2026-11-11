package br.com.hub.adapter.out.persistence;

import br.com.hub.application.port.out.FaturaRepositoryPort;
import br.com.hub.domain.model.Fatura;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador de saída de persistência (simulado em memória).
 * Para usar um banco real basta criar outro adaptador que implemente
 * FaturaRepositoryPort (ex.: JPA) – o núcleo não muda.
 */
public class FaturaRepositoryEmMemoriaAdapter implements FaturaRepositoryPort {

    private final Map<String, Fatura> banco = new ConcurrentHashMap<>();

    @Override
    public Fatura salvar(Fatura fatura) {
        banco.put(fatura.getId(), fatura);
        return fatura;
    }

    @Override
    public Optional<Fatura> buscarPorId(String id) {
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public List<Fatura> listar() {
        List<Fatura> faturas = new ArrayList<>(banco.values());
        faturas.sort(Comparator.comparing(Fatura::getCriadaEm).reversed());
        return faturas;
    }
}
