# Análise de Estilos Arquiteturais

## 1. Arquitetura Cliente-Servidor

### Conceito e Definição
O estilo **Cliente-Servidor** é um padrão arquitetural que divide o sistema em duas entidades principais com papéis bem definidos:
* **Cliente:** O solicitante do serviço ou recurso. É responsável pela interface com o usuário e pelo envio de requisições.
* **Servidor:** O provedor do serviço. É responsável por processar as requisições recebidas, executar as regras de negócio, gerenciar dados e retornar as respostas aos clientes.

A comunicação ocorre tipicamente por meio de protocolos de rede padronizados (como HTTP/HTTPS, TCP/IP, gRPC) seguindo um modelo de requisição-resposta (*request-response*).

---

### Casos de Uso Comuns
Este padrão é amplamente recomendado para aplicações centralizadas onde múltiplos usuários precisam acessar recursos compartilhados:
1. **Aplicações Web e APIs REST:** Navegadores ou aplicativos mobile (clientes) consumindo dados e serviços de uma API central hospedada em nuvem.
2. **Servidores de Banco de Dados:** Aplicações de negócio (clientes) conectando-se a um SGBD (PostgreSQL, MySQL, SQL Server) para consulta e persistência de dados.

---

### Principais Vantagens
* **Centralização de Regras e Dados:** Facilidade para atualizar a lógica de negócios, aplicar políticas de segurança e gerenciar backups em um único ponto central.
* **Separação de Responsabilidades:** O cliente foca na experiência do usuário (UI/UX) enquanto o servidor foca em processamento e integridade dos dados.
* **Manutenção Simplificada:** Alterações na camada de servidor não exigem, necessariamente, atualizações diretas no cliente (desde que o contrato da interface/API seja mantido).

---

### Principais Desvantagens
* **Ponto Único de Falha (SPOF):** Se o servidor cair ou ficar indisponível, todos os clientes dependentes dele param de funcionar.
* **Gargalos de Desempenho e Escalabilidade:** Conforme o número de requisições concorrentes cresce, o servidor pode sobrecarregar, exigindo estratégias de balanceamento de carga e escalabilidade vertical/horizontal.
* **Dependência de Conectividade:** A maioria das operações exige conexão contínua e estável com a rede para comunicação com o servidor.

---
---

## 2. Arquitetura Publicador/Assinante (Pub/Sub)

### Conceito e Definição
O estilo **Publicador/Assinante (Pub/Sub)** é um padrão arquitetural orientado a eventos e mensagens, focado no desacoplamento entre emissores e receptores:
* **Publicador (Publisher):** Emite eventos ou mensagens sem conhecimento prévio de quem irá recebê-los ou processá-los.
* **Canal/Tópico (Broker/Message Bus):** Componente intermediário que recebe as mensagens e as distribui para os interessados.
* **Assinante (Subscriber):** Registra interesse em tópicos específicos e consome as mensagens conforme elas são publicadas.

A comunicação é predominantemente **assíncrona**, garantindo que as partes não precisem estar ativas ao mesmo tempo nem conheçam a identidade umas das outras (*desacoplamento espacial e temporal*).

---

### Casos de Uso Comuns
Recomendado para sistemas altamente dinâmicos, distribuídos e que exigem reação a eventos em tempo real:
1. **Sistemas de E-commerce (Processamento de Pedidos):** Ao confirmar uma compra, o evento `PedidoCriado` é publicado; simultaneamente, os serviços de envio de e-mail, faturamento e separação de estoque consomem o evento de forma independente.
2. **Plataformas de Telemetria e IoT:** Milhares de sensores publicam métricas (temperatura, localização GPS) em tópicos gerenciados por corretores como Apache Kafka ou RabbitMQ para análise em tempo real.

---

### Principais Vantagens
* **Alto Desacoplamento:** Produtores e consumidores evoluem e escalam de forma totalmente independente.
* **Escalabilidade e Resiliência:** Novos consumidores podem ser adicionados sem impactar os produtores; falhas temporárias em um consumidor não afetam a publicação nem os demais consumidores.
* **Comunicação 1-para-N (Broadcast):** Uma única mensagem publicada pode ser consumida simultaneamente por dezenas de sistemas distintos.

---

### Principais Desvantagens
* **Complexidade Operacional:** Introduz a necessidade de manter e monitorar um *message broker* (ex.: RabbitMQ, Apache Kafka, AWS SNS/SQS).
* **Consistência Eventual:** Como o processamento é assíncrono, os dados podem demorar frações de segundo (ou mais) para refletir em todo o ecossistema.
* **Dificuldade de Rastreabilidade e Debugging:** O fluxo de execução não é linear, tornando o rastreamento de erros e o fluxo de mensagens entre múltiplos serviços mais desafiador.
