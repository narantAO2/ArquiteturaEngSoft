package br.com.hub.adapter.out.notafiscal;

import br.com.hub.application.port.out.EmissaoNotaFiscalPort;
import br.com.hub.domain.model.Fatura;
import br.com.hub.domain.model.ItemPedido;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adaptador de saída SIMULADO: emissão de nota fiscal por SOAP.
 *
 * Monta o envelope SOAP que seria enviado ao web service da SEFAZ,
 * simula a resposta de autorização e extrai dela a chave de acesso (44 dígitos).
 */
public class NotaFiscalSoapAdapter implements EmissaoNotaFiscalPort {

    private static final System.Logger LOG = System.getLogger(NotaFiscalSoapAdapter.class.getName());
    private static final String ENDPOINT = "https://homologacao.sefaz.simulada/ws/NFeAutorizacao4";
    private static final String UF_PR = "41";
    private static final String CNPJ_EMITENTE = "12345678000195";
    private static final String MODELO_NFE = "55";
    private static final String SERIE = "001";
    private static final Pattern CHAVE = Pattern.compile("<chNFe>(\\d{44})</chNFe>");

    private final AtomicLong numeroNota = new AtomicLong(0);

    @Override
    public String emitir(Fatura fatura) {
        String requisicao = montarEnvelope(fatura);
        LOG.log(System.Logger.Level.INFO, "[SOAP] POST {0}\n{1}", ENDPOINT, requisicao);

        String resposta = simularServicoSefaz();
        LOG.log(System.Logger.Level.INFO, "[SOAP] resposta\n{0}", resposta);

        Matcher matcher = CHAVE.matcher(resposta);
        if (!matcher.find()) {
            throw new IllegalStateException("Resposta SOAP sem chave de acesso.");
        }
        return matcher.group(1);
    }

    private String montarEnvelope(Fatura fatura) {
        StringBuilder itens = new StringBuilder();
        for (ItemPedido item : fatura.getPedido().itens()) {
            itens.append("          <det><xProd>").append(item.descricao())
                    .append("</xProd><qCom>").append(item.quantidade())
                    .append("</qCom><vUnCom>").append(item.valorUnitario())
                    .append("</vUnCom></det>\n");
        }
        return """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:nfe="http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4">
                  <soapenv:Header/>
                  <soapenv:Body>
                    <nfe:nfeDadosMsg>
                      <NFe>
                        <infNFe>
                          <ide><cUF>%s</cUF><mod>%s</mod><serie>%s</serie></ide>
                          <emit><CNPJ>%s</CNPJ></emit>
                          <dest><xNome>%s</xNome><CPF>%s</CPF><email>%s</email></dest>
                %s          <total><vNF>%s</vNF></total>
                          <infAdic><infCpl>Fatura %s / Pedido %s</infCpl></infAdic>
                        </infNFe>
                      </NFe>
                    </nfe:nfeDadosMsg>
                  </soapenv:Body>
                </soapenv:Envelope>""".formatted(
                UF_PR, MODELO_NFE, SERIE, CNPJ_EMITENTE,
                fatura.getPedido().cliente().nome(),
                fatura.getPedido().cliente().documento(),
                fatura.getPedido().cliente().email(),
                itens, fatura.getValorTotal(),
                fatura.getId(), fatura.getPedido().pedidoId());
    }

    /** Simula o web service autorizando a nota e devolvendo o protocolo com a chave. */
    private String simularServicoSefaz() {
        String chave = gerarChaveAcesso(numeroNota.incrementAndGet());
        return """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <nfeResultMsg>
                      <retEnviNFe><cStat>100</cStat><xMotivo>Autorizado o uso da NF-e</xMotivo>
                        <protNFe><infProt><chNFe>%s</chNFe><nProt>1412600000%05d</nProt></infProt></protNFe>
                      </retEnviNFe>
                    </nfeResultMsg>
                  </soap:Body>
                </soap:Envelope>""".formatted(chave, numeroNota.get());
    }

    /**
     * Chave de acesso NF-e (44 dígitos):
     * cUF(2) AAMM(4) CNPJ(14) mod(2) serie(3) nNF(9) tpEmis(1) cNF(8) cDV(1).
     */
    static String gerarChaveAcesso(long numero) {
        String aamm = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        String nNF = String.format("%09d", numero);
        String cNF = String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        String semDv = UF_PR + aamm + CNPJ_EMITENTE + MODELO_NFE + SERIE + nNF + "1" + cNF;
        return semDv + digitoVerificador(semDv);
    }

    /** DV da chave: módulo 11 com pesos 2 a 9; resto 0 ou 1 gera DV 0. */
    static int digitoVerificador(String numero) {
        int soma = 0;
        int peso = 2;
        for (int i = numero.length() - 1; i >= 0; i--) {
            soma += Character.getNumericValue(numero.charAt(i)) * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
