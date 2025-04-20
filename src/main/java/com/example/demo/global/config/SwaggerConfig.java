package com.example.demo.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("인턴쉽 API")
                        .description("API 설명 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("박무근")
                                .email("sohappynow12@naver.com")
                                .url("https://github.com/parkmookeun"))
                        .license(new License()
                                .name("라이센스")
                                .url("라이센스 URL")));
    }
}
