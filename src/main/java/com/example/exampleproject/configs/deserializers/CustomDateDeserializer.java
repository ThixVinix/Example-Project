package com.example.exampleproject.configs.deserializers;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.messages.MessageUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@SuppressWarnings("unused")
public class CustomDateDeserializer extends JsonDeserializer<Date> {

    private static final String EXPECTED_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final SimpleDateFormat formatter;

    public CustomDateDeserializer() {
        this.formatter = new SimpleDateFormat(EXPECTED_FORMAT);
    }

    /**
     * Deserializes a JSON string into a Date object using a specific date format.
     *
     * @param p    The JsonParser instance used to parse the JSON content.
     * @param ctxt The DeserializationContext that can be used to access information about the deserialization process.
     * @return The deserialized Date object.
     * @throws IOException       If there is an error during parsing or if the date is formatted incorrectly.
     * @throws BusinessException if the input string cannot be parsed into a Date
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
                    MessageUtils.getMessage("invalid.datetime.format",
                            p.getParsingContext().getCurrentName(),
                            date,
                            EXPECTED_FORMAT));
        }
    }
}