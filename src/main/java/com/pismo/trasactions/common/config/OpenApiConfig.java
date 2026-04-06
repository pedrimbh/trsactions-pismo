package com.pismo.trasactions.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Transactions API",
                version = "1.0.0",
                description = "API do desafio Pismo para gerenciamento de contas e transações financeiras. "
                        + "Permite criar e consultar contas, além de registrar transações com os tipos: "
                        + "1 = Compra à Vista (negativo), 2 = Compra Parcelada (negativo), "
                        + "3 = Saque (negativo), 4 = Pagamento (positivo).",
                contact = @Contact(
                        name = "Pismo Challenge",
                        email = "contato@pismo.io"
                )
        )
)
@Configuration
public class OpenApiConfig {
}

