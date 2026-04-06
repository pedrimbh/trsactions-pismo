package com.pismo.trasactions.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "Estrutura padrão de erro retornada pela API")
public class ApiError {

    @Schema(description = "Momento em que o erro ocorreu", example = "2026-04-05T10:00:00")
    private final LocalDateTime timestamp;

    @Schema(description = "Código HTTP do erro", example = "400")
    private final int status;

    @Schema(description = "Mensagem descritiva do erro", example = "document_number must contain exactly 11 digits")
    private final String message;

    @JsonProperty("path")
    @Schema(description = "Caminho da requisição que gerou o erro", example = "/accounts")
    private final String requestPath;

    public ApiError(int status, String message, String requestPath) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.requestPath = requestPath;
    }

}
