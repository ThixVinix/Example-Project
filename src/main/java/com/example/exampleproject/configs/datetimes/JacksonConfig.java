package com.example.exampleproject.configs.datetimes;

import com.example.exampleproject.configs.datetimes.deserializers.*;
import com.example.exampleproject.configs.datetimes.serializers.*;
import com.example.exampleproject.utils.ZoneUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.*;
import java.util.Date;

/**
 * Configures custom Jackson serialization and deserialization for date and time types.
 * It registers a {@link JavaTimeModule} with specific serializers and deserializers
 * and defines the date and time formats using predefined patterns and the project's time zone.
 */
@Configuration
public class JacksonConfig {

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

        ZoneId projectZoneId = ZoneUtils.getProjectZoneId();

        JavaTimeModule module = new JavaTimeModule();

        module.addSerializer(LocalDate.class, new CustomLocalDateSerializer(projectZoneId));
        module.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer());

        module.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer(projectZoneId));
        module.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());

        module.addSerializer(ZonedDateTime.class, new CustomZonedDateTimeSerializer(projectZoneId));
        module.addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer());

        module.addSerializer(LocalTime.class, new CustomLocalTimeSerializer(projectZoneId));
        module.addDeserializer(LocalTime.class, new CustomLocalTimeDeserializer());

        module.addSerializer(Date.class, new CustomDateSerializer(projectZoneId));
        module.addDeserializer(Date.class, new CustomDateDeserializer());

        return module;
    }

}
