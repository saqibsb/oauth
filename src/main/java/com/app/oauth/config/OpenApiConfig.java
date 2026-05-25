package com.app.oauth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OAuth Template API")
                        .version("1.0.0")
                        .description("Reusable Spring Boot OAuth template with JWT auth")
                        .contact(new Contact().name("Your Name").email("you@example.com"))
                        .license(new License().name("MIT")));
    }
}