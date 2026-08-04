package io.github.thixvinix.commons.spring;

import io.github.thixvinix.commons.i18n.CompositeMessageResolver;
import io.github.thixvinix.commons.i18n.MessageBundleContribution;
import io.github.thixvinix.commons.i18n.MessageResolver;
import io.github.thixvinix.commons.i18n.Messages;
import io.github.thixvinix.commons.i18n.ResourceBundleMessageResolver;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Wires {@code io.github.thixvinix.commons.i18n.Messages} to the application's own
 * {@link MessageSource} and {@code LocaleContextHolder}, so every thixvinix-commons validation
 * module resolves messages through the same pipeline the rest of the application uses — and so an
 * application {@code messages_*.properties} can override any library key by simply declaring it.
 * <p>
 * Disable with {@code thixvinix.commons.i18n.enabled=false} to keep the libraries on their
 * standalone {@link ResourceBundleMessageResolver} default instead.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "thixvinix.commons.i18n", name = "enabled", matchIfMissing = true)
public class CommonsI18nAutoConfiguration {

    @Bean
    public InitializingBean commonsI18nWiring(MessageSource messageSource) {
        return () -> wire(messageSource);
    }

    private void wire(MessageSource messageSource) {
        Messages.setLocaleProvider(new SpringLocaleContextProvider());

        List<String> libraryBasenames = new ArrayList<>();
        ServiceLoader.load(MessageBundleContribution.class)
                .forEach(contribution -> libraryBasenames.addAll(contribution.basenames()));

        List<String> overlayAndLibraryBasenames = new ArrayList<>();
        overlayAndLibraryBasenames.add("commons-messages");
        overlayAndLibraryBasenames.addAll(libraryBasenames);

        List<MessageResolver> resolvers = new ArrayList<>();
        resolvers.add(new CommonsMessageSourceResolver(messageSource));
        ServiceLoader.load(MessageResolver.class).forEach(resolvers::add);
        resolvers.add(new ResourceBundleMessageResolver(overlayAndLibraryBasenames));

        Messages.setResolver(new CompositeMessageResolver(resolvers));

        if (messageSource instanceof AbstractResourceBasedMessageSource resourceBasedMessageSource) {
            for (String basename : libraryBasenames) {
                resourceBasedMessageSource.addBasenames("classpath:" + basename);
            }
        }
    }
}
