package io.github.thixvinix.commons.dates;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.ServiceLoader;

/**
 * Converts the date/time types supported by {@code @DateRangeValidation} to {@link Instant} for
 * comparison, resolving the zone via {@link ZoneIdProvider} for types with no zone of their own.
 * <p>
 * {@link #setZoneIdProvider(ZoneIdProvider)} lets a framework integration (e.g. the Spring Boot
 * starter, whose provider needs constructor-injected configuration and therefore cannot be
 * discovered via {@link ServiceLoader}) install itself directly; {@link #reset()} restores
 * {@code ServiceLoader} discovery.
 */
public final class TemporalConversions {

    private static volatile ZoneIdProvider zoneIdProvider;

    private TemporalConversions() {
    }

    /**
     * @throws IllegalArgumentException if {@code dateObject} is null or of an unsupported type
     */
    public static Instant toInstant(Object dateObject) {
        return toInstant(dateObject, currentZoneId());
    }

    public static void setZoneIdProvider(ZoneIdProvider provider) {
        zoneIdProvider = provider;
    }

    /**
     * Restores {@code ServiceLoader}-discovered defaults. Intended for test teardown.
     */
    public static synchronized void reset() {
        zoneIdProvider = null;
    }

    /**
     * @throws IllegalArgumentException if {@code dateObject} is null or of an unsupported type
     */
    public static Instant toInstant(Object dateObject, ZoneId zoneId) {
        if (dateObject instanceof LocalDate localDate) {
            return localDate.atStartOfDay(zoneId).toInstant();
        }
        if (dateObject instanceof LocalDateTime localDateTime) {
            return localDateTime.atZone(zoneId).toInstant();
        }
        if (dateObject instanceof ZonedDateTime zonedDateTime) {
            return zonedDateTime.withZoneSameInstant(zoneId).toInstant();
        }
        if (dateObject instanceof Date date) {
            return date.toInstant().atZone(zoneId).toInstant();
        }
        throw new IllegalArgumentException("Unsupported date type: " + dateObject);
    }

    private static ZoneId currentZoneId() {
        ZoneIdProvider current = zoneIdProvider;
        if (current == null) {
            synchronized (TemporalConversions.class) {
                current = zoneIdProvider;
                if (current == null) {
                    current = buildDefaultZoneIdProvider();
                    zoneIdProvider = current;
                }
            }
        }
        return current.currentZoneId();
    }

    private static ZoneIdProvider buildDefaultZoneIdProvider() {
        return ServiceLoader.load(ZoneIdProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .max(Comparator.comparingInt(ZoneIdProvider::order))
                .orElseGet(DefaultZoneIdProvider::new);
    }
}
