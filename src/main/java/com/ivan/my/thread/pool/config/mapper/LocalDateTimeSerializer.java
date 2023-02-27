package com.ivan.my.thread.pool.config.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ivan.my.thread.pool.util.constant.DatePattern.DATE_TIME;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String dateStr = localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME));
        gen.writeString(dateStr);
    }

}
