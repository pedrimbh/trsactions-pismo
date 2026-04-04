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

## 6. Estrategia de teste em camadas

A cobertura esta organizada por responsabilidade, sem nenhum teste redundante entre camadas:

### Camada HTTP — `AccountControllerTest`, `TransactionControllerTest`

Usam `MockMvcBuilders.standaloneSetup()` com `@Mock` (Mockito):

- Validam status HTTP (201, 200, 400, 404, 422)
- Validam serializacao JSON (campos com snake_case)
- Validam regras de `@Valid` (campos nulos, formato incorreto, valor zero)
- Validam respostas do `ApiExceptionHandler`
- Nao sobem contexto Spring nem banco — executam em milissegundos

> Nota: `@WebMvcTest` nao esta disponivel no Spring Boot 4.x. O `standaloneSetup` cobre
> o mesmo escopo de forma direta e sem dependencias de auto-configuracao.

### Camada de servico — `AccountServiceTest`, `TransactionServiceTest`

Usam `@ExtendWith(MockitoExtension.class)`:

- Testam regras de negocio isoladas (normalizacao de sinal, duplicidade, tipos invalidos)
- Repositorios sao mockados — sem banco real
- Cobrem todos os caminhos: sucesso, excecoes esperadas, comportamento de borda

### Dominio puro — `OperationTypeTest`

JUnit 5 simples, sem Spring:

- Testa o enum `OperationType` para todos os IDs validos e invalidos
- Usa `@ParameterizedTest` para cobrir os tres tipos negativos (1, 2, 3)

### Contexto Spring — `TrasactionsApplicationTests`

Unico teste com `@SpringBootTest` que verifica que o contexto sobe sem erros.

Motivo da separacao: cada camada e testada na menor granularidade possivel, mantendo
os testes rapidos e o feedback preciso em caso de falha.

## 7. Concorrencia com Virtual Threads (Project Loom)

Ativado via `spring.threads.virtual.enabled=true` no `application.properties`.

**Como funciona:**

- Cada requisicao HTTP recebe uma **virtual thread** (gerenciada pela JVM, nao pelo SO)
- Virtual threads sao levissimas — a JVM pode criar milhoes sem overhead significativo
- Quando uma virtual thread bloqueia esperando o banco (JPA/JDBC), a **thread do SO e liberada** para processar outras requisicoes
- O codigo continua identico — sem `Mono`, `Flux` ou padroes reativos

**Por que nao WebFlux:**

WebFlux (Spring Reactive) exigiria reescrever controllers, services e repositorios inteiros, alem de trocar JPA por R2DBC. Para o escopo deste projeto, o ganho nao justifica a complexidade. Virtual Threads entregam beneficio equivalente sem nenhuma mudanca no codigo de negocio.

**HikariCP configurado junto:**

Com virtual threads, milhares de requisicoes podem ser processadas simultaneamente — mas todas disputam o mesmo pool de conexoes com o banco. Sem configurar o pool, o gargalo migra de CPU/threads para conexoes ociosas.

- Local (H2): pool de 5 conexoes (suficiente para desenvolvimento)
- Docker (PostgreSQL): pool de 20 conexoes com timeouts definidos

## Trade-offs

- Nao foi criado um modulo separado para `operation_types`; o enum atende bem ao escopo do desafio.
- Nao foi usado mensageria/event sourcing para manter simplicidade na entrega.
- Swagger pode ser adicionado em etapa seguinte como bonus de documentacao interativa.


