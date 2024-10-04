package com.example.exampleproject.configs.deserializers;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.messages.MessageUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@SuppressWarnings("unused")
public class CustomZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    private static final String EXPECTED_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private final DateTimeFormatter formatter;

    public CustomZonedDateTimeDeserializer() {
        this.formatter = DateTimeFormatter.ofPattern(EXPECTED_FORMAT);
    }

    /**
     * Deserializes JSON content into a {@link ZonedDateTime} object.
     *
     * @param p    the {@link JsonParser} used to read JSON content.
     * @param ctxt the {@link DeserializationContext} that can be used to access contextual information about the
     *             deserialization process.
     * @return a {@link ZonedDateTime} instance parsed from the JSON string.
     * @throws IOException       if there is an issue during parsing or reading JSON content.
     * @throws BusinessException if the input string cannot be parsed into a {@link ZonedDateTime}
     */
    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        String date = p.getText();

        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        try {
            return ZonedDateTime.parse(date, formatter);
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