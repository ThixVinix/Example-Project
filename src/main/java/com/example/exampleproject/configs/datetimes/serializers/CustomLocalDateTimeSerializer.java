package com.example.exampleproject.configs.datetimes.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Custom serializer for {@link LocalDateTime} that converts the LocalDateTime to a JSON string
 * representation based on a specified time zone and a predefined format.
 */
@Slf4j
public class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private static final String EXPECTED_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private final DateTimeFormatter formatter;
    private final ZoneId zoneId;

    public CustomLocalDateTimeSerializer(ZoneId zoneId) {
        this.formatter = DateTimeFormatter.ofPattern(EXPECTED_FORMAT);
        this.zoneId = zoneId;
    }

    /**
     * Serializes a {@link LocalDateTime} object into a JSON string representation of a date.
     *
     * @param value  the {@link LocalDateTime} object to serialize
     * @param gen    the JSON generator used to write the serialized value
     * @param serializers the serializer provider
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            String dateString = value.atZone(zoneId).format(formatter);
            gen.writeString(dateString);
        }
    }
}