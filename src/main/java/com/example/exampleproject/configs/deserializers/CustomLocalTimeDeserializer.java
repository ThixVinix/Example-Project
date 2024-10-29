package com.example.exampleproject.configs.deserializers;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.MessageUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@SuppressWarnings("unused")
public class CustomLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter EXPECTED_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String time = p.getText();

        if (time == null || time.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalTime.parse(time, EXPECTED_FORMAT);
        } catch (DateTimeParseException e) {
            log.warn(e.getMessage(), e);
            throw new BusinessException(
                    MessageUtils.getMessage("msg.deserialization.invalid.datetime.format",
                            p.getParsingContext().getCurrentName(),
                            time,
                            EXPECTED_FORMAT));
        }
    }
}
