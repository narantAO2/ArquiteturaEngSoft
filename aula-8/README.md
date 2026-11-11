# Aula 8 – Integrando a venda ao hub

Projeto `mvc` (o mesmo da aula 6, com produtos e estoque) com o novo fluxo de **pedidos**. Ao **fechar um pedido**, a venda:

1. dá baixa no estoque dos itens;
2. envia o pedido ao hub de faturamento da aula 7 via REST (`POST {hub.url}/faturas`);
3. guarda o **ID da fatura** devolvido e marca o pedido como **FECHADO**.

Arquivos principais:

- `mvc/src/main/java/br/com/mvc/integracao/HubFaturamentoClient.java`: chamada REST (`RestClient`) com timeouts
- `mvc/src/main/java/br/com/mvc/service/PedidoService.java`: método `fechar`
- `mvc/src/main/java/br/com/mvc/model/Pedido.java`: campo `faturaId`
- `analise-integracao.md`: análise dos problemas e das soluções

## Como executar

1. Suba o hub da aula 7 (porta 8081): `cd ../aula-7/hub && ./gradlew bootRun`
2. Suba a venda (porta 8080): `cd mvc && ./gradlew bootRun`
3. Em http://localhost:8080:
   1. cadastre um produto;
   2. dê entrada no estoque para ele ficar DISPONÍVEL;
   3. crie um pedido e adicione itens;
   4. clique em **Fechar pedido e faturar**.

   O ID da fatura aparece no pedido e pode ser consultado em `http://localhost:8081/faturas/{id}`.

O endereço do hub fica em `application.properties` (`hub.url`). Para testar o problema 1 da análise, basta fechar um pedido com o hub desligado.
