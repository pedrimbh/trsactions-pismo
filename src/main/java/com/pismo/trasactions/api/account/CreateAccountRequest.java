package com.pismo.trasactions.api.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Dados para criação de uma nova conta")
public class CreateAccountRequest {

    @JsonProperty("document_number")
    @NotBlank(message = "must not be blank")
    @Pattern(regexp = "\\d{11}", message = "must contain exactly 11 digits")
    @Schema(description = "Número do documento (CPF) — exatamente 11 dígitos numéricos", example = "12345678900", pattern = "\\d{11}")
    private String documentNumber;

}
