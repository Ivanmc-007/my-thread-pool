package com.ivan.my.thread.pool.config.jacksonmodule;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ivan.my.thread.pool.config.mapper.LocalDateDeserializer;
import com.ivan.my.thread.pool.config.mapper.LocalDateSerializer;
import com.ivan.my.thread.pool.config.mapper.LocalDateTimeDeserializer;
import com.ivan.my.thread.pool.config.mapper.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomJavaTimeModule {

    private static final JavaTimeModule moduleLocalDateTime = new JavaTimeModule();

    static {
        moduleLocalDateTime.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        moduleLocalDateTime.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        moduleLocalDateTime.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        moduleLocalDateTime.addSerializer(LocalDate.class, new LocalDateSerializer());
    }

    private CustomJavaTimeModule() {
    }

    public static JavaTimeModule getInstance() {
        return moduleLocalDateTime;
    }

}
