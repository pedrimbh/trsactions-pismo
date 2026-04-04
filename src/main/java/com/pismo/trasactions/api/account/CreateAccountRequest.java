package com.pismo.trasactions.api.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateAccountRequest {

    @JsonProperty("document_number")
    @NotBlank(message = "must not be blank")
    @Pattern(regexp = "\\d{11}", message = "must contain exactly 11 digits")
    private String documentNumber;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}

