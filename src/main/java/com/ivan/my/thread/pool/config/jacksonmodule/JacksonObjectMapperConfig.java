package com.ivan.my.thread.pool.config.jacksonmodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonObjectMapperConfig {

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(CustomJavaTimeModule.getInstance())  // add global module for date,dateTime
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

}
