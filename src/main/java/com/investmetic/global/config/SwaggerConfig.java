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
        // SecurityScheme 설정 추가 (Bearer Token 인증)
        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // OpenAPI 구성 설정
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("인증 토큰", bearerAuthScheme))
                .addSecurityItem(new SecurityRequirement().addList("인증 토큰"))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("InvestMetic API")
                .description("API")
                .version("1.0.0");
    }
}