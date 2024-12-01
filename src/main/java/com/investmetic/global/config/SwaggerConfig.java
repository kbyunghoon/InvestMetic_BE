package com.investmetic.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme accessTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("access-token")
                .description("액세스 토큰 - Bearer까지 포함하여 등록");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("access-token", accessTokenScheme))
                .addSecurityItem(new SecurityRequirement().addList("access-token"))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("InvestMetic API")
                .description("API")
                .version("1.0.0");
    }
}