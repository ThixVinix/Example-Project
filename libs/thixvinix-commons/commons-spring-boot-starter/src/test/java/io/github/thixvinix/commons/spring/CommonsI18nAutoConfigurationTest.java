package io.github.thixvinix.commons.spring;

import io.github.thixvinix.commons.i18n.Messages;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommonsI18nAutoConfiguration Tests")
class CommonsI18nAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonsI18nAutoConfiguration.class))
            .withUserConfiguration(AppMessageSourceConfig.class);

    @AfterEach
    void resetMessages() {
        Messages.reset();
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    @DisplayName("Wires an InitializingBean that installs a resolver and locale provider once the context refreshes")
    void wiresMessagesOnContextRefresh() {
        contextRunner.run((AssertableApplicationContext context) -> {
            assertThat(context).hasSingleBean(MessageSource.class);

            assertThat(Messages.get(Locale.US, "app.own.key"))
                    .isEqualTo("Hello from the application's own MessageSource.");
        });
    }

    @Test
    @DisplayName("Messages.get() without an explicit locale follows LocaleContextHolder")
    void followsLocaleContextHolder() {
        contextRunner.run((AssertableApplicationContext context) -> {
            LocaleContextHolder.setLocale(Locale.US);
            assertThat(Messages.get("app.own.key"))
                    .isEqualTo("Hello from the application's own MessageSource.");
        });
    }

    @Test
    @DisplayName("thixvinix.commons.i18n.enabled=false disables the autoconfiguration")
    void canBeDisabledByProperty() {
        contextRunner.withPropertyValues("thixvinix.commons.i18n.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(CommonsI18nAutoConfiguration.class));
    }

    @Configuration
    static class AppMessageSourceConfig {

        @Bean
        MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:app-messages");
            messageSource.setDefaultEncoding("UTF-8");
            return messageSource;
        }
    }
}
