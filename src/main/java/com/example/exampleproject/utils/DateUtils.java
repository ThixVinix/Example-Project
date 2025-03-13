package com.example.exampleproject.utils;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.time.*;

import java.util.Date;
import java.util.Objects;

/**
 * Utility class providing methods and constants for handling date and time objects
 * in various formats. This class contains methods to validate date ranges and
 * convert between different types of date representations, as well as format specifications
 * for serialization and deserialization purposes.
 *
 * <p>This class is not meant to be instantiated as all its methods are static.
 * Attempting to instantiate this class will result in an {@link IllegalStateException}.
 */
@Slf4j
public class DateUtils {

    public static final String DATE_SERIALIZER_FORMAT = "dd/MM/yyyy";
    public static final String LOCAL_DATE_SERIALIZER_FORMAT = "dd/MM/yyyy";
    public static final String LOCAL_DATE_TIME_SERIALIZER_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String LOCAL_TIME_SERIALIZER_FORMAT = "HH:mm:ss";
    public static final String ZONED_DATE_TIME_SERIALIZER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final String DATE_DESERIALIZER_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LOCAL_DATE_DESERIALIZER_FORMAT = "yyyy-MM-dd";
    public static final String LOCAL_DATE_TIME_DESERIALIZER_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LOCAL_TIME_DESERIALIZER_FORMAT = "HH:mm:ss";
    public static final String ZONED_DATE_TIME_DESERIALIZER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private DateUtils() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    /**
     * Checks if the provided date range is valid, ensuring that both dates are not null and dateA is
     * not after dateB.
     *
     * @param dateA     the start date of the range; can be of type {@link LocalDate},
     *                  {@link LocalDateTime}, {@link ZonedDateTime}, or {@link Date}
     * @param dateAName the name of the start date field for use in error messages
     * @param dateB     the end date of the range; can be of type {@link LocalDate},
     *                  {@link LocalDateTime}, {@link ZonedDateTime}, or {@link Date}
     * @param dateBName the name of the end date field for use in error messages
     * @throws BusinessException if either dateA or dateB is null, or if dateA is after dateB
     */
    public static void checkDateRange(Object dateA, String dateAName, Object dateB, String dateBName) {
        boolean bothDatesNull = Objects.isNull(dateA) && Objects.isNull(dateB);
        boolean eitherDateNull = Objects.isNull(dateA) || Objects.isNull(dateB);

        if (bothDatesNull) {
            return;
        }

        if (eitherDateNull) {
            throw new BusinessException(
                    MessageUtils.getMessage("msg.validation.request.field.date.range.empty", dateAName, dateBName)
            );
        }

        validateDateRange(dateA, dateAName, dateB, dateBName);
    }

    private static void validateDateRange(Object dateA, String dateAName, Object dateB, String dateBName) {
        try {
            Instant instantA = toInstant(dateA);
            Instant instantB = toInstant(dateB);

            if (instantA.isAfter(instantB)) {
                throw new BusinessException(
                        MessageUtils.getMessage(
                                "msg.validation.request.field.date.range.invalid", dateAName, dateBName
                        )
                );
            }
        } catch (Exception e) {
            log.warn("Error validating date range: {}", e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Converts various date/time types to Instant.
     *
     * @param dateObject the date/time object to convert
     * @return the Instant representation of the date/time object
     * @throws IllegalArgumentException Unsupported date type
     */
    public static Instant toInstant(Object dateObject) throws IllegalArgumentException {
        ZoneId projectZoneId = ZoneUtils.getProjectZoneId();
        return switch (dateObject) {
            case LocalDate localDate -> localDate.atStartOfDay(projectZoneId).toInstant();
            case LocalDateTime localDateTime -> localDateTime.atZone(projectZoneId).toInstant();
            case ZonedDateTime zonedDateTime -> zonedDateTime.withZoneSameInstant(projectZoneId).toInstant();
            case Date date -> date.toInstant().atZone(projectZoneId).toInstant();
            case null, default -> throw new IllegalArgumentException("Unsupported date type: " + dateObject);
        };
    }

}
