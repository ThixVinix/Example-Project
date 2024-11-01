package com.example.exampleproject.configs.datetimes.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Custom serializer for {@link ZonedDateTime} that converts the ZonedDateTime
 * to a JSON string representation based on a specified time zone and a predefined format.
 */
@Slf4j
public class CustomZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            .appendLiteral(' ')
            .optionalStart()
            .appendZoneId()
            .optionalEnd()
            .toFormatter();

    private final ZoneId zoneId;

    public CustomZonedDateTimeSerializer(ZoneId zoneId) {
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
            ZonedDateTime zonedValue = value.withZoneSameInstant(zoneId);
            String dateString = zonedValue.format(FORMATTER);
            gen.writeString(dateString);
        }
    }
}