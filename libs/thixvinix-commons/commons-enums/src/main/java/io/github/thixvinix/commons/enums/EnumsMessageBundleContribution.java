package io.github.thixvinix.commons.enums;

import io.github.thixvinix.commons.i18n.MessageBundleContribution;

import java.util.List;

public final class EnumsMessageBundleContribution implements MessageBundleContribution {

    @Override
    public List<String> basenames() {
        return List.of("io/github/thixvinix/commons/enums/messages");
    }
}
