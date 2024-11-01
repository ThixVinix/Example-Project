package com.example.exampleproject.configs.datetimes.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom serializer for {@link ZonedDateTime} that converts the ZonedDateTime
 * to a JSON string representation based on a specified time zone and a predefined format.
 */
@Slf4j
public class CustomZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    private static final String EXPECTED_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private final DateTimeFormatter formatter;
    private final ZoneId zoneId;

    public CustomZonedDateTimeSerializer(ZoneId zoneId) {
        this.formatter = DateTimeFormatter.ofPattern(EXPECTED_FORMAT);
        this.zoneId = zoneId;
    }

    /**
     * Serializes a {@link ZonedDateTime} object into a JSON string representation of a date.
     *
     * @param value  the {@link ZonedDateTime} object to serialize
     * @param gen    the JSON generator used to write the serialized value
     * @param serializers the serializer provider
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            String dateString = value.withZoneSameLocal(zoneId).format(formatter);
            gen.writeString(dateString);
        }
    }
}