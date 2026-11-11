package br.com.mvc.integracao;

import br.com.mvc.model.ItemPedido;
import br.com.mvc.model.Pedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.List;

/**
 * Cliente REST do hub de faturamento (aula 7).
 * Envia o pedido fechado para POST {hub.url}/faturas e devolve o ID da fatura.
 */
@Component
public class HubFaturamentoClient {

    private final RestClient restClient;

    public HubFaturamentoClient(@Value("${hub.url}") String hubUrl,
                                @Value("${hub.timeout-conexao-ms:2000}") long timeoutConexaoMs,
                                @Value("${hub.timeout-leitura-ms:5000}") long timeoutLeituraMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(timeoutConexaoMs));
        requestFactory.setReadTimeout(Duration.ofMillis(timeoutLeituraMs));
        this.restClient = RestClient.builder()
                .baseUrl(hubUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public String faturar(Pedido pedido) {
        FaturamentoRequest request = paraRequest(pedido);
        try {
            FaturamentoResponse response = restClient.post()
                    .uri("/faturas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(FaturamentoResponse.class);
            if (response == null || response.faturaId() == null) {
                throw new IntegracaoHubException("O hub respondeu sem o ID da fatura.", null);
            }
            return response.faturaId();
        } catch (ResourceAccessException e) {
            throw new IntegracaoHubException(
                    "Hub de faturamento indisponível ou sem resposta (" + e.getMessage() + ").", e);
        } catch (RestClientResponseException e) {
            throw new IntegracaoHubException("O hub recusou o faturamento: HTTP "
                    + e.getStatusCode().value() + " " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new IntegracaoHubException("Erro ao chamar o hub de faturamento: " + e.getMessage(), e);
        }
    }

    private FaturamentoRequest paraRequest(Pedido pedido) {
        List<FaturamentoRequest.Item> itens = pedido.getItens().stream()
                .map(this::paraItem)
                .toList();
        return new FaturamentoRequest(
                String.valueOf(pedido.getId()),
                new FaturamentoRequest.Cliente(pedido.getClienteNome(), pedido.getClienteEmail(),
                        pedido.getClienteDocumento()),
                itens);
    }

    private FaturamentoRequest.Item paraItem(ItemPedido item) {
        return new FaturamentoRequest.Item(item.getProduto().getNome(), item.getQuantidade(),
                item.getValorUnitario());
    }
}
