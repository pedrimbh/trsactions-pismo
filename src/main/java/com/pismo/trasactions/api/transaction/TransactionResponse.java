package com.pismo.trasactions.api.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    @JsonProperty("transaction_id")
    private final Long transactionId;

    @JsonProperty("account_id")
    private final Long accountId;

    @JsonProperty("operation_type_id")
    private final Integer operationTypeId;

    private final BigDecimal amount;

    @JsonProperty("event_date")
    private final LocalDateTime eventDate;

    public TransactionResponse(Long transactionId, Long accountId, Integer operationTypeId, BigDecimal amount,
            LocalDateTime eventDate) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.operationTypeId = operationTypeId;
        this.amount = amount;
        this.eventDate = eventDate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Integer getOperationTypeId() {
        return operationTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }
}

