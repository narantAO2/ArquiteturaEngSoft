package br.com.hub.domain.model;

public enum StatusFatura {
    /** Fatura criada e persistida (RF01). */
    CRIADA,
    /** Boleto, nota fiscal e e-mail concluídos (RF02, RF03 e RF04). */
    FATURADA
}
