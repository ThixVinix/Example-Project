package com.example.exampleproject.configs.datetimes.deserializers;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.MessageUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A custom deserializer for {@link LocalDate}, which expects the input date string
 * to be in the format {@value EXPECTED_FORMAT}.
 */
@Slf4j
public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final String EXPECTED_FORMAT = "yyyy-MM-dd";

    private final DateTimeFormatter formatter;

    public CustomLocalDateDeserializer() {
        this.formatter = DateTimeFormatter.ofPattern(EXPECTED_FORMAT);
    }

    /**
     * Deserializes a JSON string representation of a date into a {@link LocalDate} object.
     *
     * @param p    the JSON parser to read the date string from
     * @param ctxt the deserialization context
     * @return the deserialized {@link LocalDate} object, or null if the input string is null or empty
     * @throws IOException       if an I/O error occurs during deserialization
     * @throws BusinessException if the input string cannot be parsed into a {@link LocalDate}
     */
    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        String date = p.getText();

        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            log.warn(e.getMessage(), e);
            throw new BusinessException(
                    MessageUtils.getMessage("msg.deserialization.invalid.datetime.format",
                            p.getParsingContext().getCurrentName(),
                            date,
                            EXPECTED_FORMAT));
        }
    }
}