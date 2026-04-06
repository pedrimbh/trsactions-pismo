# Transactions API

API REST para o desafio de rotina de transações da Pismo.

## Objetivo

Implementar os endpoints:

- `POST /accounts` — cria conta
- `GET /accounts/{accountId}` — consulta conta
- `POST /transactions` — cria transação

Regras de negócio aplicadas:

| `operation_type_id` | Operação | Sinal do valor |
|---|---|---|
| 1 | Compra à Vista | Negativo |
| 2 | Compra Parcelada | Negativo |
| 3 | Saque | Negativo |
| 4 | Pagamento | Positivo |

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA
- H2 Database (local/testes)
- PostgreSQL (Docker)
- Docker + Docker Compose
- JUnit 5 + Mockito + MockMvc
- Springdoc OpenAPI 3 (Swagger UI)

## Estrutura resumida

```
api/
  account/     → AccountController, CreateAccountRequest, AccountResponse
  transaction/ → TransactionController, CreateTransactionRequest, TransactionResponse
service/       → regras de negócio
domain/        → entidades JPA e enum OperationType
repository/    → interfaces Spring Data JPA
common/
  exception/   → ApiExceptionHandler, ApiError, BusinessException, ResourceNotFoundException
  config/      → OpenApiConfig (Swagger/OpenAPI)
```

Detalhes de decisões em [`docs/architecture.md`](docs/architecture.md).

## Como executar

### 1) Rodar com Docker (recomendado)

Pré-requisito: Docker Desktop instalado e rodando.

```bash
docker compose up --build
```

Aguarde a mensagem `Started TrasactionsApplication` no log.  
A API estará disponível em `http://localhost:8080`.

Para parar:

```bash
docker compose down
```

Para parar e apagar os dados do banco:

```bash
docker compose down -v
```

### 2) Rodar testes localmente

A suite possui **44 testes** organizados em camadas:

| Camada | Arquivo(s) | Tipo |
|---|---|---|
| HTTP | `AccountControllerTest`, `TransactionControllerTest` | MockMvc (sem Spring context) |
| Service | `AccountServiceTest`, `TransactionServiceTest` | Mockito |
| Domínio | `OperationTypeTest` | JUnit puro |
| Contexto | `TrasactionsApplicationTests` | @SpringBootTest |

```powershell
Set-Location "C:\Users\Joao Pedro\Desktop\PISMO"
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd test
```

### 3) Subir localmente com H2

```powershell
Set-Location "C:\Users\Joao Pedro\Desktop\PISMO"
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd spring-boot:run
```

Aplicação sobe em `http://localhost:8080` com banco H2 em memória.

## Swagger UI

A documentação interativa da API está disponível após subir a aplicação:

| Recurso | URL |
|---|---|
| **Swagger UI (visual)** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |
| **OpenAPI YAML** | `http://localhost:8080/v3/api-docs.yaml` |

> O Swagger UI permite testar os endpoints diretamente no browser, com exemplos de request/response e descrição de todos os campos e códigos de resposta.

## Endpoints

### Criar conta

`POST /accounts`

Request:

```json
{
  "document_number": "12345678900"
}
```

| Campo | Tipo | Regra |
|---|---|---|
| `document_number` | string | Obrigatório — exatamente 11 dígitos numéricos (CPF) |

Respostas:

| Código | Descrição |
|---|---|
| `201 Created` | Conta criada com sucesso |
| `400 Bad Request` | Campo ausente ou formato inválido |
| `422 Unprocessable Entity` | Regra de negócio violada |

Response (201):

```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

---

### Buscar conta

`GET /accounts/{accountId}`

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `accountId` | Long (path) | Identificador único da conta |

Respostas:

| Código | Descrição |
|---|---|
| `200 OK` | Conta encontrada |
| `404 Not Found` | Conta não existe para o ID informado |

Response (200):

```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

---

### Criar transação

`POST /transactions`

Request:

```json
{
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

| Campo | Tipo | Regra |
|---|---|---|
| `account_id` | Long | Obrigatório |
| `operation_type_id` | Integer | Obrigatório — valores válidos: 1, 2, 3 ou 4 |
| `amount` | Decimal | Obrigatório — mínimo 0.01 (o sinal é aplicado automaticamente pelo tipo de operação) |

Respostas:

| Código | Descrição |
|---|---|
| `201 Created` | Transação criada com sucesso |
| `400 Bad Request` | Campo ausente, valor zero/negativo ou `operation_type_id` fora do intervalo 1–4 |
| `404 Not Found` | Conta não encontrada para o `account_id` informado |

Response (201):

```json
{
  "transaction_id": 1,
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45,
  "event_date": "2026-04-05T12:00:00"
}
```

---

### Estrutura de erro (todas as rotas)

```json
{
  "timestamp": "2026-04-05T10:00:00",
  "status": 400,
  "message": "document_number must contain exactly 11 digits",
  "path": "/accounts"
}
```

## H2 Console (apenas local sem Docker)

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:trasactionsdb`
- User: `sa`
- Password: *(vazio)*
