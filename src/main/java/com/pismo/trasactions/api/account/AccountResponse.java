package com.pismo.trasactions.api.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Dados da conta retornada")
public class AccountResponse {

    @JsonProperty("account_id")
    @Schema(description = "Identificador único da conta", example = "1")
    private final Long accountId;

    @JsonProperty("document_number")
    @Schema(description = "Número do documento (CPF)", example = "12345678900")
    private final String documentNumber;

    public AccountResponse(Long accountId, String documentNumber) {
        this.accountId = accountId;
        this.documentNumber = documentNumber;
    }

}
