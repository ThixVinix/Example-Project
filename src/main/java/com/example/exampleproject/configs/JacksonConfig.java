package com.example.exampleproject.configs;

import com.example.exampleproject.configs.datetimes.deserializers.*;
import com.example.exampleproject.configs.datetimes.serializers.*;
import com.example.exampleproject.utils.ZoneUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.*;
import java.util.Date;

/**
 * Configuration class for Jackson ObjectMapper.
 * Sets up custom serialization and deserialization for Java time types
 * and configures various Jackson features.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(createJavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }

    /**
     * Configures a custom serialization and deserialization
     * for dates and times. This method registers a {@link JavaTimeModule} with specific
     * serializers and deserializers for the following types:
     * <ul>
     * <li>{@link LocalDate}</li>
     * <li>{@link LocalDateTime}</li>
     * <li>{@link ZonedDateTime}</li>
     * <li>{@link LocalTime}</li>
     * <li>{@link Date}</li>
     * </ul>
     * The date and time formats are derived from predefined patterns and the project's time zone.
     *
     * @return a configured {@link JavaTimeModule} instance
     */
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
