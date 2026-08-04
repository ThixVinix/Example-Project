package io.github.thixvinix.commons.spring;

import io.github.thixvinix.commons.dates.ZoneIdProvider;

import java.time.DateTimeException;
import java.time.ZoneId;

/**
 * Resolves the zone used by {@code commons-dates}' {@code @DateRangeValidation} from
 * {@code spring.jackson.time-zone}, matching how the rest of a Spring Boot application already
 * interprets zone-less dates. Falls back to UTC if unset or invalid.
 */
public final class SpringZoneIdProvider implements ZoneIdProvider {

    private final ZoneId zoneId;

    public SpringZoneIdProvider(String configuredZoneId) {
        this.zoneId = parse(configuredZoneId);
    }

    private static ZoneId parse(String configuredZoneId) {
        if (configuredZoneId == null || configuredZoneId.isBlank()) {
            return ZoneId.of("UTC");
        }
        try {
            return ZoneId.of(configuredZoneId);
        } catch (DateTimeException e) {
            return ZoneId.of("UTC");
        }
    }

    @Override
    public ZoneId currentZoneId() {
        return zoneId;
    }

    @Override
    public int order() {
        return 100;
    }
}
