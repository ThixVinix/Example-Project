package com.example.exampleproject.configs.deserializers;

import com.example.exampleproject.utils.ZoneUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Configures custom Jackson serialization and deserialization for date and time types.
 * It registers a {@link JavaTimeModule} with specific serializers and deserializers
 * and defines the date and time formats using predefined patterns and the project's time zone.
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    private static final String DATE_TIME_FORMAT_PATTERN = "dd/MM/yyyy HH:mm:ss";
    private static final String ZONED_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String TIME_FORMAT_PATTERN = "HH:mm:ss";

    /**
     * Configures an {@link ObjectMapper} to handle custom serialization and deserialization
     * for dates and times. This method registers a {@link JavaTimeModule} with specific
     * serializers and deserializers for the following types:
     * <ul>
     * <li>{@link LocalDate}</li>
     * <li>{@link LocalDateTime}</li>
     * <li>{@link ZonedDateTime}</li>
     * <li<{@link LocalTime}
     * <li>{@link Date}</li>
     * </ul>
     * The date and time formats are derived from predefined patterns and the project's time zone.
     *
     * @return a configured {@link ObjectMapper} instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = createJavaTimeModule();
        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private JavaTimeModule createJavaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();

        DateTimeFormatter dateFormatter = createFormatter(DATE_FORMAT_PATTERN);
        module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        module.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer());

        DateTimeFormatter dateTimeFormatter = createFormatter(DATE_TIME_FORMAT_PATTERN);
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        module.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());

        DateTimeFormatter zonedDateTimeFormatter = createFormatter(ZONED_DATE_TIME_FORMAT_PATTERN);
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(zonedDateTimeFormatter));
        module.addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer());

        DateTimeFormatter timeFormatter = createFormatter(TIME_FORMAT_PATTERN);
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        module.addDeserializer(LocalTime.class, new CustomLocalTimeDeserializer());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        module.addSerializer(Date.class, new DateSerializer(false, simpleDateFormat));
        module.addDeserializer(Date.class, new CustomDateDeserializer());

        return module;
    }

    private DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneUtils.getProjectZoneId());
    }
}
