package com.pismo.trasactions.api.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateTransactionRequest {

    @JsonProperty("account_id")
    @NotNull(message = "must not be null")
    private Long accountId;

    @JsonProperty("operation_type_id")
    @NotNull(message = "must not be null")
    private Integer operationTypeId;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0.01", message = "must be greater than zero")
    private BigDecimal amount;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(Integer operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

