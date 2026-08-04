package io.github.thixvinix.commons.spring;

import io.github.thixvinix.commons.dates.TemporalConversions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommonsDatesAutoConfiguration Tests")
class CommonsDatesAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonsDatesAutoConfiguration.class));

    @AfterEach
    void reset() {
        TemporalConversions.reset();
    }

    @Test
    @DisplayName("Wires a ZoneIdProvider backed by spring.jackson.time-zone")
    void wiresZoneIdFromProperty() {
        contextRunner.withPropertyValues("spring.jackson.time-zone=America/Sao_Paulo")
                .run(context -> {
                    LocalDate date = LocalDate.of(2024, 1, 1);
                    Instant instant = TemporalConversions.toInstant(date);
                    Instant expected = date.atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant();

                    assertThat(instant).isEqualTo(expected);
                });
    }

    @Test
    @DisplayName("Falls back to UTC when spring.jackson.time-zone is not set")
    void fallsBackToUtcWhenPropertyMissing() {
        contextRunner.run(context -> {
            LocalDate date = LocalDate.of(2024, 1, 1);
            Instant instant = TemporalConversions.toInstant(date);
            Instant expected = date.atStartOfDay(ZoneId.of("UTC")).toInstant();

            assertThat(instant).isEqualTo(expected);
        });
    }
}
