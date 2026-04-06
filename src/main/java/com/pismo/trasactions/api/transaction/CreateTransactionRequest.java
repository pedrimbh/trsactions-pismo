package com.pismo.trasactions.api.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Schema(description = "Dados para criação de uma nova transação")
public class CreateTransactionRequest {

    @JsonProperty("account_id")
    @NotNull(message = "must not be null")
    @Schema(description = "Identificador da conta vinculada à transação", example = "1")
    private Long accountId;

    @JsonProperty("operation_type_id")
    @NotNull(message = "must not be null")
    @Schema(
            description = "Tipo de operação: 1 = Compra à Vista (negativo), 2 = Compra Parcelada (negativo), "
                    + "3 = Saque (negativo), 4 = Pagamento (positivo)",
            example = "4",
            allowableValues = {"1", "2", "3", "4"}
    )
    private Integer operationTypeId;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0.01", message = "must be greater than zero")
    @Schema(description = "Valor da transação — deve ser maior que zero (o sinal é aplicado automaticamente pelo tipo de operação)", example = "123.45")
    private BigDecimal amount;

}
