package com.commerce.abm.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ABM E-Commerce API",
                version = "1.0",
                description = "Application CRUD of Clients, Products, Carts and Invoices"),
        tags = {
                @Tag(name = "Clients paths definitions", description = "CRUD of Client class controller"),
                @Tag(name = "Products paths definitions", description = "CRUD of Product class controller"),
                @Tag(name = "Carts paths definitions", description = "CRUD of Cart class controller"),
                @Tag(name = "Invoices paths definitions", description = "CRUD of Invoice class controller")
        }
)
public class OpenApi {
        @Bean
        public OpenApiCustomiser customOpenApi() {
                return openApi -> {
                        List<String> customOrder = List.of(
                                "Clients paths definitions",
                                "Products paths definitions",
                                "Carts paths definitions",
                                "Invoices paths definitions"
                        );
                        List<io.swagger.v3.oas.models.tags.Tag> orderedTags = new ArrayList<>();
                        for (String tagName : customOrder) {
                                openApi.getTags().stream()
                                        .filter(tag -> tag.getName().equals(tagName))
                                        .findFirst()
                                        .ifPresent(orderedTags::add);
                        }
                        openApi.getTags().stream()
                                .filter(tag -> !customOrder.contains(tag.getName()))
                                .forEach(orderedTags::add);
                        openApi.setTags(orderedTags);
                        System.out.println("Ordered Tags: " + openApi.getTags());
                };
        }

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI();
        }
}
