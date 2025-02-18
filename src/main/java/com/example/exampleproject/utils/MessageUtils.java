package com.example.exampleproject.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.Locale;

/**
 * Utility class for retrieving localized messages using Spring's {@link MessageSource}.
 * It supports message retrieval in different locales, including Brazilian Portuguese, English,
 * and the locale derived from the current context.
 * This class is designed as a singleton and initializes the static {@code MessageSource}
 * during the application lifecycle.
 */
@Component
public class MessageUtils {

    private static MessageSource messageSourceStatic;

    private final MessageSource messageSource;

    @Autowired
    private MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @PostConstruct
    private synchronized void init() {
        messageSourceStatic = messageSource;
    }


    /**
     * Retrieves a localized message for the given key in the default locale (Brazilian Portuguese).
     *
     * @param key the message key to look up, such as 'error.notfound'
     * @return the resolved message as a String in Brazilian Portuguese
     */
    @SuppressWarnings("unused")
    public static String getBrazilianPortugueseMessage(String key) {
        return messageSourceStatic.getMessage(key, null, Locale.of("pt", "BR"));
    }

    /**
     * Retrieves a localized message for the given key and arguments in the default locale (Brazilian Portuguese).
     *
     * @param key  the message key to look up, such as 'error.notfound'
     * @param args an array of arguments that will be filled in for params within the message (optional)
     * @return the resolved message as a String in Brazilian Portuguese
     */
    @SuppressWarnings("unused")
    public static String getBrazilianPortugueseMessage(String key, Object... args) {
        return messageSourceStatic.getMessage(key, args, Locale.of("pt", "BR"));
    }


    /**
     * Retrieves a localized message for the given key in the English locale.
     *
     * @param key the message key to look up, such as 'error.notfound'
     * @return the resolved message as a String in English
     */
    @SuppressWarnings("unused")
    public static String getEnglishMessage(String key) {
        return messageSourceStatic.getMessage(key, null, Locale.US);
    }

    /**
     * Retrieves a localized message for the given key and arguments in English locale.
     *
     * @param key  the message key to look up, such as 'error.notfound'
     * @param args an array of arguments that will be filled in for params within the message (optional)
     * @return the resolved message as a String in English
     */
    @SuppressWarnings("unused")
    public static String getEnglishMessage(String key, Object... args) {
        return messageSourceStatic.getMessage(key, args, Locale.US);
    }

    /**
     * Retrieves a localized message for the given key and locale.
     *
     * @param key the message key to look up, such as 'error.notfound'
     * @return the resolved message as a String
     */
    @SuppressWarnings("unused")
    public static String getMessage(String key) {
        return messageSourceStatic.getMessage(key, null, LocaleContextHolder.getLocale());
    }


    /**
     * Retrieves a localized message for the given key and locale, with optional arguments.
     *
     * @param key  the message key to look up, such as 'error.notfound'
     * @param args an array of arguments that will be filled in for params within the message (optional)
     * @return the resolved message as a String
     */
    @SuppressWarnings("unused")
    public static String getMessage(String key, Object... args) {
        return messageSourceStatic.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
