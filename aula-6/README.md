# Aula 6 – Módulo de estoque

Projeto `mvc` (Spring Boot + Thymeleaf + H2) com o fluxo de **produto** e o novo fluxo de **entrada e saída de estoque**.

## Regras implementadas

- O produto tem um status: `DISPONIVEL`, `INDISPONIVEL` ou `DESCONTINUADO`.
- Todo produto criado começa como **INDISPONÍVEL** e com estoque 0.
- Somente o **Administrador** pode descontinuar um produto.
- Um produto **descontinuado** não pode ser movimentado no estoque.
- Entrada: quando o saldo sai de 0, o produto fica **DISPONÍVEL**.
- Saída: quando o saldo chega a 0, o produto fica **INDISPONÍVEL**. Não é permitido sair sem saldo ou mais do que o saldo.

As regras ficam na entidade `Produto` (`registrarEntrada`, `registrarSaida`, `descontinuar`) e são orquestradas pelo `EstoqueService` e pelo `ProdutoService`. Cada movimentação é gravada em `MovimentacaoEstoque`.

O "usuário logado" é simulado com um seletor de perfil no topo da tela (Administrador, Vendedor ou Equipe de Estoque), que fica guardado na sessão.

## Estrutura

```
mvc/src/main/java/br/com/mvc
├── controller   HomeController, ProdutoController, EstoqueController, PerfilAtual
├── model        Produto, StatusProduto, MovimentacaoEstoque, TipoMovimentacao, Perfil
├── repository   ProdutoRepository, MovimentacaoEstoqueRepository
├── service      ProdutoService, EstoqueService
└── exception    RegraNegocioException
mvc/src/main/resources/view   index, produtos/lista, produtos/form, estoque/estoque
```

## Diagramas (`diagramas/`)

| Arquivo | Conteúdo |
|---|---|
| `sequencia-entrada-estoque.mermaid` | Sequência da rotina de entrada de estoque |
| `sequencia-saida-estoque.mermaid` | Sequência da rotina de saída de estoque |
| `atividades-saida-estoque.mermaid` | Atividades da saída (bloqueia descontinuado e sem estoque) |
| `estados-produto.mermaid` | Estados do produto e suas transições |

## Como executar

```bash
cd mvc
./gradlew bootRun        # Windows: gradlew.bat bootRun
./gradlew test           # testes das regras do produto
```

Acesse http://localhost:8080/home (H2 console em `/h2-console`, JDBC URL `jdbc:h2:mem:vendas`).
