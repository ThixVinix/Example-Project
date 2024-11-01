package com.example.exampleproject.configs.datetimes.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * Custom serializer for {@link Date} that converts the Date object to a JSON string representation
 * using a predefined format and time zone.
 */
@Slf4j
public class CustomDateSerializer extends JsonSerializer<Date> {

    private static final String EXPECTED_FORMAT = "dd/MM/yyyy";

    private final SimpleDateFormat formatter;

    public CustomDateSerializer(ZoneId zoneId) {
        this.formatter = new SimpleDateFormat(EXPECTED_FORMAT);
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        this.formatter.setTimeZone(timeZone);
    }

    /**
     * Serializes a {@link Date} object into a JSON string representation of a date.
     *
     * @param value  the {@link Date} object to serialize
     * @param gen    the JSON generator used to write the serialized value
     * @param serializers the serializer provider
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            String dateString = formatter.format(value);
            gen.writeString(dateString);
        }
    }
}
