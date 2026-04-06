package com.pismo.trasactions.api.account;

import com.pismo.trasactions.common.exception.ApiError;
import com.pismo.trasactions.service.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Operações de gerenciamento de contas")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar uma nova conta",
            description = "Cria uma conta com base no número do documento (CPF). Retorna os dados da conta criada."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Conta criada com sucesso",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos (campo obrigatório ausente ou formato incorreto)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Regra de negócio violada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.create(request.getDocumentNumber());
    }

    @GetMapping("/{accountId}")
    @Operation(
            summary = "Buscar conta por ID",
            description = "Retorna os dados de uma conta existente a partir do seu identificador único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Conta encontrada",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta não encontrada para o ID informado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    public AccountResponse findById(
            @Parameter(description = "Identificador único da conta", example = "1", required = true)
            @PathVariable Long accountId) {
        return accountService.findById(accountId);
    }
}
