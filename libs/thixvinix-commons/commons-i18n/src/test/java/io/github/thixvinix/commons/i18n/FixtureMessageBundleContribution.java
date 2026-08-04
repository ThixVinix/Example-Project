package io.github.thixvinix.commons.i18n;

import java.util.List;

/**
 * Registered via {@code META-INF/services} to prove {@link Messages} discovers module-contributed
 * bundles without any manual wiring.
 */
public final class FixtureMessageBundleContribution implements MessageBundleContribution {

    @Override
    public List<String> basenames() {
        return List.of("io/github/thixvinix/commons/i18n/fixture/messages");
    }
}
