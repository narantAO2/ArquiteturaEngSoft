package br.com.hub.adapter.out.boleto;

import br.com.hub.application.port.out.EmissaoBoletoPort;
import br.com.hub.domain.model.Fatura;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Adaptador de saída SIMULADO: emissão de boleto por FILA.
 *
 * Simula um broker (ex.: RabbitMQ) com uma fila em memória "boleto.emitir":
 * o hub publica a mensagem e um consumidor simulado (o "banco") a processa,
 * devolvendo o código de barras de 44 dígitos no padrão FEBRABAN.
 */
public class BoletoFilaAdapter implements EmissaoBoletoPort {

    public static final String FILA = "boleto.emitir";
    private static final System.Logger LOG = System.getLogger(BoletoFilaAdapter.class.getName());
    private static final LocalDate DATA_BASE_FATOR = LocalDate.of(1997, 10, 7);
    private static final String BANCO = "001";   // código do banco (simulado)
    private static final String MOEDA = "9";     // real

    record MensagemBoleto(String faturaId, String documentoPagador, BigDecimal valor, LocalDate vencimento) {
    }

    private final BlockingQueue<MensagemBoleto> fila = new LinkedBlockingQueue<>();

    @Override
    public String emitir(Fatura fatura) {
        MensagemBoleto mensagem = new MensagemBoleto(
                fatura.getId(),
                fatura.getPedido().cliente().documento(),
                fatura.getValorTotal(),
                LocalDate.now().plusDays(3));

        publicar(mensagem);
        return consumir();
    }

    private void publicar(MensagemBoleto mensagem) {
        fila.offer(mensagem);
        LOG.log(System.Logger.Level.INFO, "[FILA {0}] mensagem publicada para a fatura {1} (valor {2})",
                FILA, mensagem.faturaId(), mensagem.valor());
    }

    /** Consumidor simulado do banco: lê a mensagem da fila e gera o código de barras. */
    private String consumir() {
        MensagemBoleto mensagem = fila.poll();
        if (mensagem == null) {
            throw new IllegalStateException("Fila " + FILA + " vazia.");
        }
        String codigoBarras = gerarCodigoBarras(mensagem.valor(), mensagem.vencimento());
        LOG.log(System.Logger.Level.INFO, "[FILA {0}] boleto emitido para a fatura {1}: {2}",
                FILA, mensagem.faturaId(), codigoBarras);
        return codigoBarras;
    }

    /** Código de barras FEBRABAN: banco(3) moeda(1) DV(1) fator(4) valor(10) campo livre(25) = 44. */
    static String gerarCodigoBarras(BigDecimal valor, LocalDate vencimento) {
        String fator = String.format("%04d", fatorVencimento(vencimento));
        long centavos = valor.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
        String valorFormatado = String.format("%010d", centavos);
        StringBuilder campoLivre = new StringBuilder();
        for (int i = 0; i < 25; i++) {
            campoLivre.append(ThreadLocalRandom.current().nextInt(10));
        }
        String semDv = BANCO + MOEDA + fator + valorFormatado + campoLivre;
        int dv = digitoVerificadorModulo11(semDv);
        return BANCO + MOEDA + dv + fator + valorFormatado + campoLivre;
    }

    /** Fator de vencimento (reinicia em 1000 após atingir 9999, regra FEBRABAN de 2025). */
    static int fatorVencimento(LocalDate vencimento) {
        long dias = ChronoUnit.DAYS.between(DATA_BASE_FATOR, vencimento);
        if (dias <= 9999) {
            return (int) dias;
        }
        return (int) (((dias - 10000) % 9000) + 1000);
    }

    /** DV geral do código de barras: módulo 11 com pesos 2 a 9; resultados 0, 10 e 11 viram 1. */
    static int digitoVerificadorModulo11(String numero) {
        int soma = 0;
        int peso = 2;
        for (int i = numero.length() - 1; i >= 0; i--) {
            soma += Character.getNumericValue(numero.charAt(i)) * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }
        int dv = 11 - (soma % 11);
        return (dv == 0 || dv == 10 || dv == 11) ? 1 : dv;
    }
}
