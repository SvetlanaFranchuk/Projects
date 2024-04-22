package org.example.pizzeria.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        info = @Info(
                title = "Pizzeria",
                description = "Final qualifying work",
                version = "1.0.0",
                contact = @Contact(
                        name = "Svitlana Franchuk",
                        email = "franchuk.sa77@gmail.com",
                        url = "https://github.com/SvetlanaFranchuk/Projects/tree/main/Pizzeria"
                )
        )
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearer-key"));
    }

    @Bean
    public GroupedOpenApi appApi() {
        return GroupedOpenApi.builder()
                .group("allApp")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi favoritesApi() {
        return GroupedOpenApi.builder()
                .group("favorites")
                .pathsToMatch("/favorites/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reviewApi() {
        return GroupedOpenApi.builder()
                .group("review")
                .pathsToMatch("/review/**")
                .build();
    }

    @Bean
    public GroupedOpenApi doughApi() {
        return GroupedOpenApi.builder()
                .group("dough")
                .pathsToMatch("/dough/**")
                .build();
    }

    @Bean
    public GroupedOpenApi ingredientApi() {
        return GroupedOpenApi.builder()
                .group("ingredient")
                .pathsToMatch("/ingredient/**")
                .build();
    }

    @Bean
    public GroupedOpenApi pizzaApi() {
        return GroupedOpenApi.builder()
                .group("pizza")
                .pathsToMatch("/pizza/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminUserApi() {
        return GroupedOpenApi.builder()
                .group("adminUser")
                .pathsToMatch("/admin/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("order")
                .pathsToMatch("/order/**")
                .build();
    }

    @Bean
    public GroupedOpenApi statApi() {
        return GroupedOpenApi.builder()
                .group("stat")
                .pathsToMatch("/stat/**")
                .build();
    }
}


