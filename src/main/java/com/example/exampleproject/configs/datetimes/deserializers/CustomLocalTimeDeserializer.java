package com.example.exampleproject.configs.datetimes.deserializers;

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

/**
 * A custom deserializer for {@link LocalTime}, which expects the input date string
 * to be in the format {@value EXPECTED_FORMAT}.
 */
@Slf4j
public class CustomLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    private static final String EXPECTED_FORMAT = "HH:mm:ss";

    private final DateTimeFormatter formatter;

    public CustomLocalTimeDeserializer() {
        this.formatter = DateTimeFormatter.ofPattern(EXPECTED_FORMAT);
    }


    /**
     * Deserializes a JSON string into a {@link LocalTime} object.
     *
     * @param p the JSON parser
     * @param ctxt the deserialization context
     * @return the deserialized {@link LocalTime} object, or null if the input is empty or null
     * @throws IOException if an I/O error occurs during deserialization
     * @throws BusinessException if the input string cannot be parsed into a {@link LocalTime} object
     */
    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String time = p.getText();

        if (time == null || time.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalTime.parse(time, formatter);
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
