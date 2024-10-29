package com.example.exampleproject.utils;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.Date;

@Slf4j
public class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    /**
     * Checks if the provided date range is valid, ensuring that both dates are not null and dateA is
     * not after dateB.
     *
     * @param dateA the start date of the range; can be of type {@link LocalDate},
     *              {@link LocalDateTime}, {@link ZonedDateTime}, or {@link Date}
     * @param dateAName the name of the start date field for use in error messages
     * @param dateB the end date of the range; can be of type {@link LocalDate},
     *              {@link LocalDateTime}, {@link ZonedDateTime}, or {@link Date}
     * @param dateBName the name of the end date field for use in error messages
     * @throws BusinessException if either dateA or dateB is null, or if dateA is after dateB
     */
    public static void checkDateRange(Object dateA, String dateAName, Object dateB, String dateBName) {

        if (dateA == null && dateB == null) {
            return;
        }

        if (dateA == null || dateB == null) {
            throw new BusinessException(MessageUtils.getMessage(
                    "msg.validation.request.field.date.range.empty", dateAName, dateBName));
        }

        try {
            Instant instantA = toInstant(dateA);
            Instant instantB = toInstant(dateB);

            if (instantA.isAfter(instantB)) {
                throw new BusinessException(MessageUtils.getMessage(
                        "msg.validation.request.field.date.range.invalid", dateAName, dateBName));
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
    public static Instant toInstant(Object dateObject) {
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
