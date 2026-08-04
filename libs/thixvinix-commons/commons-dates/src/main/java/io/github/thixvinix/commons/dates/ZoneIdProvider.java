package io.github.thixvinix.commons.dates;

import java.time.ZoneId;

/**
 * Supplies the {@link ZoneId} used to interpret dates that carry no zone of their own
 * ({@link java.time.LocalDate}, {@link java.time.LocalDateTime}, {@link java.util.Date}).
 * The default implementation returns {@link ZoneId#systemDefault()}; the Spring starter
 * contributes one backed by {@code spring.jackson.time-zone}, with the highest
 * {@link #order()} winning.
 */
public interface ZoneIdProvider {

    ZoneId currentZoneId();

    default int order() {
        return 0;
    }
}
