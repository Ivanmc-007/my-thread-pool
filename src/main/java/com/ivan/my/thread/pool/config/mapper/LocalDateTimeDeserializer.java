package com.ivan.my.thread.pool.config.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ivan.my.thread.pool.util.constant.DatePattern.DATE_TIME;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = parser.getCodec();
        JsonNode jsonNode = oc.readTree(parser);
        return LocalDateTime.parse(jsonNode.asText(), DateTimeFormatter.ofPattern(DATE_TIME));
    }

}
