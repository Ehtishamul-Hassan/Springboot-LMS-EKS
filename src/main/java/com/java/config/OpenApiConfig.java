package com.java.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Configuration for OpenAPI documentation in a Spring Security application.
 * This class defines API metadata, server details, and security schemes.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "OpenAPI Specification - Ehtishamul",
                version = "1.0",
                description = "OpenAPI documentation for Spring Security",
                contact = @Contact(
                        name = "Ehtishamul",
                        email = "asif282hassan@gmail.com"
                        
                ),
                license = @License(
                        name = "License Name",
                        url = "https://some-url.com"
                ),
                termsOfService = "Terms of Service"
        ),
        servers = {
                @Server(
                        description = "Local Environment",
                        url = "http://localhost:8080"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
/**
 * Defines the security scheme for JWT authentication used in API requests.
 */
@SecurityScheme(
        name = "bearerAuth", // Name of the security scheme
        description = "JWT-based authentication. Include the token in the Authorization header.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT", // Format for the bearer token
        in = SecuritySchemeIn.HEADER // JWT token is passed in the HTTP header
)
public class OpenApiConfig {
    // No implementation needed; annotations define the behavior.
}
