package ru.oldranger.club.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// http://localhost:8888/api/swagger-ui.html
@Configuration
@ComponentScan(basePackages = {"ru.oldranger.club.restcontroller"})
@SecurityScheme(name = "security",
                type = SecuritySchemeType.HTTP,
                in = SecuritySchemeIn.HEADER,
                scheme = "basic",
                //bearerFormat = "jwt",
                flows = @OAuthFlows(
                    implicit = @OAuthFlow(
                        authorizationUrl = "localhost:8888/login"
                    )
                ))
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Oldranger.club API"));
    }
}

