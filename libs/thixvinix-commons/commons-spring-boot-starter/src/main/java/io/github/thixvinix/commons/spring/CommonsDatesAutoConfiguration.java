package io.github.thixvinix.commons.spring;

import io.github.thixvinix.commons.dates.TemporalConversions;
import io.github.thixvinix.commons.dates.ZoneIdProvider;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * Only active when {@code commons-dates} is on the classpath. Installs a {@link ZoneIdProvider}
 * backed by {@code spring.jackson.time-zone} — {@code commons-dates}' own {@code ServiceLoader}
 * discovery cannot find this provider on its own, since it needs a configuration value injected
 * by Spring rather than a no-argument constructor.
 */
@AutoConfiguration
@ConditionalOnClass(TemporalConversions.class)
public class CommonsDatesAutoConfiguration {

    @Bean
    public InitializingBean commonsDatesZoneWiring(
            org.springframework.core.env.Environment environment) {
        return () -> TemporalConversions.setZoneIdProvider(
                new SpringZoneIdProvider(environment.getProperty("spring.jackson.time-zone")));
    }
}
