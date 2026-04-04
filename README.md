# Trasactions API

API REST para o desafio de rotina de transacoes da Pismo.

## Objetivo

Implementar os endpoints:

- `POST /accounts` - cria conta
- `GET /accounts/{accountId}` - consulta conta
- `POST /transactions` - cria transacao

Regras de negocio aplicadas:

- `operation_type_id` 1, 2 e 3 -> valor negativo
- `operation_type_id` 4 -> valor positivo

## Tecnologias

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- JUnit + MockMvc

## Estrutura resumida

- `api/` controllers e DTOs
- `application/` regras de negocio
- `domain/` entidades e enums de dominio
- `repository/` interfaces JPA
- `common/exception/` tratamento centralizado de erros

Detalhes de decisoes em `docs/architecture.md`.

## Como executar

### 1) Rodar testes

```powershell
Set-Location "C:\Users\Joao Pedro\Desktop\projetos_java\trasactions\trasactions"
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd test
```

### 2) Subir a aplicacao

```powershell
Set-Location "C:\Users\Joao Pedro\Desktop\projetos_java\trasactions\trasactions"
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd spring-boot:run
```

Aplicacao sobe em `http://localhost:8080`.

## Endpoints

### Criar conta

`POST /accounts`

Request:

```json
{
  "document_number": "12345678900"
}
```

Response (201):

```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

### Buscar conta

`GET /accounts/1`

Response (200):

```json
{
  "account_id": 1,
  "document_number": "12345678900"
}
```

### Criar transacao

`POST /transactions`

Request:

```json
{
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45
}
```

Response (201):

```json
{
  "transaction_id": 1,
  "account_id": 1,
  "operation_type_id": 4,
  "amount": 123.45,
  "event_date": "2026-04-04T12:00:00"
}
```

## H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:trasactionsdb`
- User: `sa`
- Password: *(vazio)*

