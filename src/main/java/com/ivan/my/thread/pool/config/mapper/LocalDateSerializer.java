package com.ivan.my.thread.pool.config.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.ivan.my.thread.pool.util.constant.DatePattern.DATE;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String dateStr = localDate.format(DateTimeFormatter.ofPattern(DATE));
        jsonGenerator.writeString(dateStr);
    }
}
