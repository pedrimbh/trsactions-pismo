# Architecture Decisions

## Contexto

O projeto implementa uma API de contas e transacoes para o teste tecnico, com foco em:

- manutenibilidade
- simplicidade
- testabilidade

## Decisoes principais

## 1. Estrutura em camadas simples

Foi adotada uma separacao clara por responsabilidade:

- `api`: controllers e DTOs HTTP
- `service`: regras de negocio
- `domain`: entidades e regras de dominio (enum de operacao)
- `repository`: persistencia com Spring Data
- `common/exception`: tratamento centralizado de erros

Motivo: facilitar evolucao sem acoplamento entre transporte HTTP e regra de negocio.

## 2. Regra de sinal centralizada no service

A normalizacao do valor da transacao acontece em `TransactionService`:

- tipos 1, 2, 3 -> valor negativo
- tipo 4 -> valor positivo

Motivo: garantir consistencia independentemente do cliente que chama a API.

## 3. Persistencia relacional com H2 + JPA

- `Account` e `Transaction` mapeadas com JPA
- `Transaction` referencia `Account` por FK
- H2 em memoria para execucao local e testes rapidos

Motivo: setup rapido para avaliacao, sem dependencias externas.

## 4. Contrato de API com snake_case

Os DTOs usam `@JsonProperty` para manter o contrato pedido no desafio:

- `document_number`
- `account_id`
- `operation_type_id`
- `event_date`

Motivo: aderencia direta ao enunciado e previsibilidade para consumidor da API.

## 5. Tratamento global de erros

`ApiExceptionHandler` converte erros de dominio/validacao em respostas HTTP consistentes:

- 404 para recurso nao encontrado
- 422 para regra de negocio
- 400 para payload invalido

Motivo: reduzir duplicacao e padronizar respostas.

## 6. Estrategia de teste

Foram criados testes de integracao com `SpringBootTest` para fluxos principais:

- criar e buscar conta
- criar transacao de compra (valor negativo)
- criar transacao de pagamento (valor positivo)

Motivo: validar comportamento de ponta a ponta (controller + service + persistencia).

## Trade-offs

- Nao foi criado um modulo separado para `operation_types`; o enum atende bem ao escopo do desafio.
- Nao foi usado mensageria/event sourcing para manter simplicidade na entrega.
- Swagger pode ser adicionado em etapa seguinte como bonus de documentacao interativa.


