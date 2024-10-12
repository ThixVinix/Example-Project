package com.example.exampleproject.utils;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.ZoneId;

@Slf4j
@Component
public class ZoneUtils {

    private static ZoneId zoneId;

    @Value("${spring.jackson.time-zone}")
    private String configuredZoneId;

    @PostConstruct
    private synchronized void init() {
        try {
            zoneId = ZoneId.of(configuredZoneId);
        } catch (DateTimeException e) {
            log.error("Invalid spring.jackson.time-zone configuration in file application: {}", configuredZoneId, e);
            zoneId = null;
        }
    }

    /**
     * Returns the ZoneId associated with the application. If the ZoneId has not been initialized,
     * it logs a warning and returns the default "UTC" ZoneId.
     *
     * @return the configured ZoneId or the default "UTC" ZoneId if not initialized
     */
    public static ZoneId getProjectZoneId() {
        if (zoneId == null) {
            log.warn("ZoneId has not been initialized. Make sure ZoneUtils is properly configured.");
            return ZoneId.of("UTC");
        }
        return zoneId;
    }

}
