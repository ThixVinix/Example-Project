package io.github.thixvinix.commons.files.spring;

import io.github.thixvinix.commons.i18n.MessageBundleContribution;

import java.util.List;

public final class MultipartMessageBundleContribution implements MessageBundleContribution {

    @Override
    public List<String> basenames() {
        return List.of("io/github/thixvinix/commons/files/spring/messages");
    }
}
