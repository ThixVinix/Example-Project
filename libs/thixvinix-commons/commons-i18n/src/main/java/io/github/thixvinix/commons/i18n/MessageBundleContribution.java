package io.github.thixvinix.commons.i18n;

import java.util.List;

/**
 * Declares the {@link java.util.ResourceBundle} basenames a module contributes to the aggregated
 * message catalog. Each module registers exactly one implementation via
 * {@code META-INF/services/io.github.thixvinix.commons.i18n.MessageBundleContribution}, so adding
 * a new module never requires changes to {@code commons-i18n} itself.
 */
public interface MessageBundleContribution {

    /**
     * Basenames in classpath-resource form, e.g. {@code "io/github/thixvinix/commons/documents/messages"}.
     */
    List<String> basenames();
}
