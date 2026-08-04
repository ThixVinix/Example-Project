package io.github.thixvinix.commons.dates;

import io.github.thixvinix.commons.i18n.MessageBundleContribution;

import java.util.List;

public final class DatesMessageBundleContribution implements MessageBundleContribution {

    @Override
    public List<String> basenames() {
        return List.of("io/github/thixvinix/commons/dates/messages");
    }
}
