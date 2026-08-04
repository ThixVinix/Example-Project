package io.github.thixvinix.commons.dates;

import java.time.ZoneId;

public final class DefaultZoneIdProvider implements ZoneIdProvider {

    @Override
    public ZoneId currentZoneId() {
        return ZoneId.systemDefault();
    }
}
