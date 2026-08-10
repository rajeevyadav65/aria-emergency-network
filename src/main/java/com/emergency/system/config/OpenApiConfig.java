package com.emergency.system.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Springdoc OpenAPI configuration.
 * Swagger UI: http://localhost:8080/swagger-ui.html
 * JSON spec:  http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ariaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ARIA Emergency Network API")
                        .version("1.0.0")
                        .description("""
                                AI-powered emergency response and public help network.
                                
                                **Authentication:** Most endpoints require a Bearer JWT token.
                                Use `POST /api/auth/login` to obtain a token, then click
                                **Authorize** and enter `Bearer <your-token>`.
                                
                                **Demo credentials:**
                                - alice@demo.com / demo123
                                - admin@aria.com / admin123
                                """)
                        .contact(new Contact()
                                .name("ARIA Team")
                                .email("support@aria-emergency.example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development"),
                        new Server().url("https://api.aria-emergency.example.com").description("Production")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token obtained from /api/auth/login")));
    }
}
