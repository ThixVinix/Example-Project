package com.example.exampleproject.configs.datetimes.serializers;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Custom serializer for {@link LocalDate} that converts the LocalDate to a JSON string
 * representation based on a specified time zone and a predefined format.
 */
@Slf4j
public class CustomLocalDateSerializer extends JsonSerializer<LocalDate> {

    private static final String EXPECTED_FORMAT = "dd/MM/yyyy";

    private final DateTimeFormatter formatter;

    private final ZoneId zoneId;

    public CustomLocalDateSerializer(ZoneId zoneId) {
        this.formatter = DateTimeFormatter.ofPattern(EXPECTED_FORMAT);
        this.zoneId = zoneId;
    }

    /**
     * Serializes a {@link LocalDate} object into a JSON string representation of a date.
     *
     * @param value  the {@link LocalDate} object to serialize
     * @param gen    the JSON generator used to write the serialized value
     * @param serializers the serializer provider
     * @throws IOException if an I/O error occurs during serialization
     */
    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            String dateString = value.atStartOfDay(zoneId).format(formatter);
            gen.writeString(dateString);
        }
    }
}
