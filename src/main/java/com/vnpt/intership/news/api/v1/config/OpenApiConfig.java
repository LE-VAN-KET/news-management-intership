package com.vnpt.intership.news.api.v1.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "API News app",
        version = "v1.0.0",
        description = "Backend APi read news",
        contact = @Contact(
                name = "Teams internship backend Java at the company VNPT IT3 07/2022"),
                termsOfService = "/terms-of-service")
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "Bearer"
)
public class OpenApiConfig {

}
