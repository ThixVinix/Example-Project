package com.example.exampleproject.configs.exceptions.handler.helper;

import com.example.exampleproject.utils.messages.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Locale;
import java.util.Optional;

@Slf4j
public class ExceptionHandlerHelper {

    public static Locale getLocaleFromRequest(WebRequest request) {
        String lang = request.getParameter("lang");
        return (lang != null) ? Locale.forLanguageTag(lang) : Locale.getDefault();
    }

    public static String getBadRequestMessage(Exception ex, Locale locale) {
        switch (ex) {
            case HttpMessageNotReadableException _ -> {
                return MessageUtils.getMessage("json.malformed", locale);
            }
            case MissingServletRequestParameterException missingEx -> {
                return MessageUtils.getMessage("missing.parameter", locale, missingEx.getParameterName());
            }
            case MethodArgumentTypeMismatchException mismatchEx -> {
                return getMismatchMessage(mismatchEx, locale);
            }
            case null, default -> {
                if (ex != null && ex.getMessage() != null) {
                    return ex.getMessage();
                } else {
                    return MessageUtils.getMessage("unknown.bad.request.error", locale);
                }
            }
        }
    }

    public static String getMismatchMessage(MethodArgumentTypeMismatchException mismatchEx, Locale locale) {
        String expectedTypeName = (mismatchEx.getRequiredType() != null) ?
                mismatchEx.getRequiredType().getSimpleName() : null;

        return switch (expectedTypeName) {
            case null -> MessageUtils.getMessage(
                    "argument.type.mismatch.without.format",
                    locale,
                    mismatchEx.getName(),
                    mismatchEx.getValue());
            case "LocalDate", "LocalDateTime", "Date", "ZonedDateTime" -> handleDateMismatch(mismatchEx, locale);
            default -> MessageUtils.getMessage(
                    "argument.type.mismatch.default",
                    locale, mismatchEx.getName(),
                    expectedTypeName,
                    mismatchEx.getValue());
        };
    }

    private static String handleDateMismatch(MethodArgumentTypeMismatchException mismatchEx, Locale locale) {
        Optional<String> expectedDateFormat = getExpectedDateFormat(mismatchEx);
        return expectedDateFormat
                .map(format ->
                        MessageUtils.getMessage(
                                "argument.type.mismatch.with.format",
                                locale,
                                mismatchEx.getName(),
                                format,
                                mismatchEx.getValue()))
                .orElseGet(() ->
                        MessageUtils.getMessage(
                                "argument.type.mismatch.without.format",
                                locale,
                                mismatchEx.getName(),
                                mismatchEx.getValue()));
    }

    private static Optional<String> getExpectedDateFormat(MethodArgumentTypeMismatchException ex) {
        try {
            Method method = ex.getParameter().getMethod();

            if (method != null) {
                for (Parameter param : method.getParameters()) {
                    if (param != null && param.isAnnotationPresent(DateTimeFormat.class)) {
                        DateTimeFormat format = param.getAnnotation(DateTimeFormat.class);
                        return Optional.of(format.pattern());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error when trying to recover expected date format. {}", e.getMessage());
        }
        return Optional.empty();
    }

}
