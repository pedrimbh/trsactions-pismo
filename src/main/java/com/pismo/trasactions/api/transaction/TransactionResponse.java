package com.pismo.trasactions.api.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Schema(description = "Dados da transação criada")
public class TransactionResponse {

    @JsonProperty("transaction_id")
    @Schema(description = "Identificador único da transação", example = "1")
    private final Long transactionId;

    @JsonProperty("account_id")
    @Schema(description = "Identificador da conta vinculada", example = "1")
    private final Long accountId;

    @JsonProperty("operation_type_id")
    @Schema(description = "Tipo de operação aplicado (1–4)", example = "4")
    private final Integer operationTypeId;

    @Schema(description = "Valor da transação com o sinal aplicado (negativo para compras/saques, positivo para pagamentos)", example = "-50.00")
    private final BigDecimal amount;

    @JsonProperty("event_date")
    @Schema(description = "Data e hora em que a transação foi registrada", example = "2026-04-05T12:00:00")
    private final LocalDateTime eventDate;

    public TransactionResponse(Long transactionId, Long accountId, Integer operationTypeId, BigDecimal amount,
            LocalDateTime eventDate) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.operationTypeId = operationTypeId;
        this.amount = amount;
        this.eventDate = eventDate;
    }

}
