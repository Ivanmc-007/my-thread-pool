package com.ivan.my.thread.pool.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

//    http://localhost:9333/api/pool/swagger-ui/index.html

    @Bean
    public OpenAPI customOpenApi(@Value("${spring.application.name}") String apiTitle,
                                 @Value("${springdoc.swagger-ui.version}") String apiVersion) {
        return new OpenAPI().info(new Info()
                .title(apiTitle)
                .description("Spring Boot RESTful client service")
                .version(apiVersion));
    }
}
