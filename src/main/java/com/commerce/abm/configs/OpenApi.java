package com.commerce.abm.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ABM E-Commerce API",
                version = "1.0",
                description = "Application CRUD of Clients, Products, Carts and Invoices"
        )
)
public class OpenApi {
}
