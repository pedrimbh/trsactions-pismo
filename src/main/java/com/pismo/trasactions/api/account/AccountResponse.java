package com.pismo.trasactions.api.account;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountResponse {

    @JsonProperty("account_id")
    private final Long accountId;

    @JsonProperty("document_number")
    private final String documentNumber;

    public AccountResponse(Long accountId, String documentNumber) {
        this.accountId = accountId;
        this.documentNumber = documentNumber;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }
}

