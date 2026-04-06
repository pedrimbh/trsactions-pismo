package com.pismo.trasactions.api.transaction;

import com.pismo.trasactions.common.exception.ApiError;
import com.pismo.trasactions.service.transaction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Operações de transações financeiras")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar uma nova transação",
            description = "Registra uma transação financeira vinculada a uma conta. "
                    + "O sinal do valor é determinado pelo tipo de operação: "
                    + "1 (Compra à Vista), 2 (Compra Parcelada) e 3 (Saque) geram valor negativo; "
                    + "4 (Pagamento) gera valor positivo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Transação criada com sucesso",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos: campo obrigatório ausente, valor zerado/negativo ou operation_type_id fora do intervalo 1–4",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta não encontrada para o account_id informado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.create(request.getAccountId(), request.getOperationTypeId(), request.getAmount());
    }
}
