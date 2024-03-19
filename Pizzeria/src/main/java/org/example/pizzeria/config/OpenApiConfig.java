package org.example.pizzeria.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Pizzeria",
                description = "Final qualifying work", version = "1.0.0",
                contact = @Contact(
                        name = "Svitlana Franchuk",
                        email = "franchuk.sa77@gmail.com",
                        url = "https://github.com/SvetlanaFranchuk/Projects/tree/main/Pizzeria"
                )
        )
)
public class OpenApiConfig {
}
