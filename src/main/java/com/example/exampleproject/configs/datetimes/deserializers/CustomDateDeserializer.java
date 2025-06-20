package com.example.exampleproject.configs.datetimes.deserializers;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.DateUtils;
import com.example.exampleproject.utils.MessageUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A custom deserializer for {@link Date}, which expects the input date string
 * to be in the format {@value EXPECTED_FORMAT}.
 */
@Slf4j
public class CustomDateDeserializer extends JsonDeserializer<Date> {

    private static final String EXPECTED_FORMAT = DateUtils.DATE_DESERIALIZER_FORMAT;

    private final SimpleDateFormat formatter;

    public CustomDateDeserializer() {
        this.formatter = new SimpleDateFormat(EXPECTED_FORMAT);
    }

    /**
     * Deserializes a JSON string representation of a date and time into a {@link Date} object.
     *
     * @param p    the JSON parser to read the date and time string from
     * @param ctxt the deserialization context
     * @return the deserialized {@link Date} object, or null if the input string is null or empty
     * @throws IOException       if an I/O error occurs during deserialization
     * @throws BusinessException if the input string cannot be parsed into a {@link Date}
     */
    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        String date = p.getText();

        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            log.warn(e.getMessage(), e);
            throw new BusinessException(
                    MessageUtils.getMessage("msg.deserialization.invalid.datetime.format",
                            date,
                            EXPECTED_FORMAT),
                    p.getParsingContext().getCurrentName());
        }
    }
}
