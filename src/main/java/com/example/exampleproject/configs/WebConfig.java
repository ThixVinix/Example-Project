package com.example.exampleproject.configs;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    private static final Locale DEFAULT_LOCALE = new Locale.Builder().setLanguage("pt").setRegion("BR").build();

    @Bean
    public LocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver() {
            @Override
            public @NonNull Locale resolveLocale(@NonNull HttpServletRequest request) {
                return resolveRequestedLocale(request);
            }
        };
    }

    private Locale resolveRequestedLocale(@NonNull HttpServletRequest request) {
        String headerLang = request.getHeader(ACCEPT_LANGUAGE_HEADER);
        Locale defaultLocale = getAdjustedDefaultLocale();
        return (headerLang == null || headerLang.isEmpty())
                ? defaultLocale
                : Locale.forLanguageTag(headerLang);
    }

    private Locale getAdjustedDefaultLocale() {
        Locale defaultLocale = Locale.getDefault();
        if (!defaultLocale.equals(Locale.US) && !defaultLocale.toLanguageTag().startsWith("en")) {
            return DEFAULT_LOCALE;
        }
        return defaultLocale;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}