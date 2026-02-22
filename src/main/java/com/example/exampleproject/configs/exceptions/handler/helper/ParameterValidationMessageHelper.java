package com.example.exampleproject.configs.exceptions.handler.helper;

import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * <h1>{@link ParameterValidationMessageHelper}</h1>
 *
 * <p>Utility class responsible for generating localized error messages for parameter validation
 * exceptions. This class handles type mismatches, constraint violations, and annotation-based
 * parameter validation errors.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Generates messages for type mismatch exceptions with format hints</li>
 *   <li>Handles constraint violation exceptions with proper parameter name resolution</li>
 *   <li>Supports various request parameter annotations (RequestParam, PathVariable, etc.)</li>
 *   <li>Provides special handling for date/time type mismatches with format patterns</li>
 * </ul>
 *
 * <h2>Implementation</h2>
 * <p>This class is designed as a <strong>utility class</strong>, which means:</p>
 * <ul>
 *   <li>All methods are <code>static</code>.</li>
 *   <li>It includes a private constructor to prevent instantiation.</li>
 * </ul>
 */
@Slf4j
public class ParameterValidationMessageHelper {

    private ParameterValidationMessageHelper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    private static final String LOCAL_DATE_TYPE = "LocalDate";
    private static final String LOCAL_DATE_TIME_TYPE = "LocalDateTime";
    private static final String ZONED_DATE_TIME_TYPE = "ZonedDateTime";
    private static final String LOCAL_TIME_TYPE = "LocalTime";
    private static final String DATE_TYPE = "Date";

    private static final List<Class<? extends Annotation>> REQUEST_ANNOTATIONS_LIST = List.of(
            RequestParam.class,
            RequestHeader.class,
            PathVariable.class,
            RequestPart.class,
            CookieValue.class,
            MatrixVariable.class
    );

    /**
     * Generates error messages for method argument type mismatch exceptions.
     * Provides special handling for date/time types with format patterns.
     *
     * @param mismatchEx The MethodArgumentTypeMismatchException to process
     * @return A map containing the parameter name and its corresponding error message
     */
    public static Map<String, String> getMismatchMessage(MethodArgumentTypeMismatchException mismatchEx) {
        Optional<Parameter> parameterOptional = searchParameter(mismatchEx);

        String expectedTypeName =
                Optional.ofNullable(mismatchEx.getRequiredType())
                        .map(Class::getSimpleName)
                        .orElse(null);

        return switch (expectedTypeName) {
            case null -> Map.of(mismatchEx.getName(),
                    getMessageUtils("msg.exception.handler.argument.type.mismatch.without.format",
                            mismatchEx.getValue()));
            case LOCAL_DATE_TYPE,
                 LOCAL_DATE_TIME_TYPE,
                 ZONED_DATE_TIME_TYPE,
                 LOCAL_TIME_TYPE,
                 DATE_TYPE -> getDateTimeMismatchMessage(
                    mismatchEx, expectedTypeName, parameterOptional.orElse(null));
            default -> getDefaultMismatchMessage(mismatchEx, expectedTypeName);
        };
    }

    private static Optional<Parameter> searchParameter(MethodArgumentTypeMismatchException mismatchEx) {
        try {
            Method method = mismatchEx.getParameter().getMethod();
            if (Objects.isNull(method)) {
                return Optional.empty();
            }
            return findMatchingParameter(mismatchEx, method);
        } catch (Exception e) {
            log.warn("Error when trying to recover request type. {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<Parameter> findMatchingParameter(
            MethodArgumentTypeMismatchException mismatchEx, Method method) {
        return Arrays.stream(method.getParameters())
                .filter(param -> containsRequestTypeAnnotation(mismatchEx, param))
                .findFirst();
    }

    private static Map<String, String> getDateTimeMismatchMessage(MethodArgumentTypeMismatchException mismatchEx,
                                                                  String expectTypeName,
                                                                  Parameter parameter) {
        if (nonNull(parameter)) {
            return extractDateTimeFormatPatternMessage(mismatchEx, parameter);
        }

        return getDefaultMismatchMessage(mismatchEx, expectTypeName);
    }

    private static Map<String, String> getDefaultMismatchMessage(MethodArgumentTypeMismatchException mismatchEx,
                                                                 String expectedTypeName) {
        return Map.of(mismatchEx.getName(), getMessageUtils(
                "msg.exception.handler.argument.type.mismatch.default",
                expectedTypeName,
                mismatchEx.getValue()));
    }

    private static boolean containsRequestTypeAnnotation(MethodArgumentTypeMismatchException ex, Parameter param) {
        return REQUEST_ANNOTATIONS_LIST.stream()
                .anyMatch(annotationType -> getAnnotationValue(param, annotationType)
                        .filter(value -> value.equals(ex.getName()))
                        .isPresent());
    }

    private static Map<String, String> extractDateTimeFormatPatternMessage(MethodArgumentTypeMismatchException ex,
                                                                           Parameter param) {
        String paramName = getParamName(param);

        if (param.isAnnotationPresent(DateTimeFormat.class)) {
            DateTimeFormat dateTimeFormat = param.getAnnotation(DateTimeFormat.class);

            if (nonNull(dateTimeFormat) && !dateTimeFormat.pattern().trim().isEmpty()) {
                return Map.of(paramName, getMessageUtils(
                        "msg.exception.handler.argument.type.mismatch.with.format",
                        dateTimeFormat.pattern().trim(),
                        ex.getValue()));
            }
        }

        Optional<String> defaultDateTimePatternOptional = getDefaultDateTimePatternForType(param.getType());

        return defaultDateTimePatternOptional
                .map(s -> Map.of(paramName, getMessageUtils(
                        "msg.exception.handler.argument.type.mismatch.with.format", s, ex.getValue())))
                .orElseGet(() -> Map.of(paramName, getMessageUtils(
                        "msg.exception.handler.argument.type.mismatch.without.format", ex.getValue())));
    }

    private static Optional<String> getDefaultDateTimePatternForType(Class<?> type) {
        return switch (type.getSimpleName()) {
            case LOCAL_DATE_TYPE -> Optional.of("yyyy-MM-dd");
            case LOCAL_DATE_TIME_TYPE -> Optional.of("yyyy-MM-dd'T'HH:mm:ss");
            case ZONED_DATE_TIME_TYPE -> Optional.of("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z'");
            case LOCAL_TIME_TYPE -> Optional.of("HH:mm:ss");
            default -> Optional.empty();
        };
    }

    private static String getParamName(Parameter param) {
        return REQUEST_ANNOTATIONS_LIST.stream()
                .map(annotationClass -> getAnnotationValue(param, annotationClass))
                .flatMap(Optional::stream)
                .findFirst()
                .orElseGet(param::getName);
    }

    /**
     * Generates error messages for constraint violation exceptions.
     * Resolves parameter names from method annotations when available.
     *
     * @param ex The ConstraintViolationException to process
     * @return A map containing parameter names and their corresponding error messages
     */
    public static Map<String, String> getConstraintViolationMessage(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = getParameterName(violation);
            String errorMessage = violation.getMessage();
            if (errors.containsKey(fieldName)) {
                errorMessage = mergeErrorMessages(errors.get(fieldName), errorMessage);
            }
            errors.put(fieldName, errorMessage);
        }

        return errors;
    }

    private static String getParameterName(ConstraintViolation<?> violation) {
        try {
            return findParameterNameInMethod(violation).orElseGet(() -> getLastSegmentFromPropertyPath(violation));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getLastSegmentFromPropertyPath(violation);
        }
    }

    private static Optional<String> findParameterNameInMethod(ConstraintViolation<?> violation) {
        Object rootBean = violation.getRootBean();
        String rootMethodName = getRootMethodName(violation);
        Method matchedMethod = findMethodByName(rootBean, rootMethodName);

        if (Objects.isNull(matchedMethod)) {
            return Optional.empty();
        }

        return Arrays.stream(matchedMethod.getParameters())
                .filter(param -> violation.getPropertyPath().toString().contains(param.getName()))
                .map(ParameterValidationMessageHelper::getParamName)
                .findFirst();
    }

    private static String getRootMethodName(ConstraintViolation<?> violation) {
        return violation.getPropertyPath().toString().split("\\.")[0];
    }

    private static String getLastSegmentFromPropertyPath(ConstraintViolation<?> violation) {
        String[] fieldParts = violation.getPropertyPath().toString().split("\\.");
        return fieldParts[fieldParts.length - 1];
    }

    private static Method findMethodByName(Object rootBean, String methodName) {
        for (Method method : rootBean.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    /**
     * Merges two error messages into a single string, separated by a semicolon.
     *
     * @param existingValue The existing error message
     * @param newValue      The new error message to append
     * @return The merged error message
     */
    public static String mergeErrorMessages(String existingValue, String newValue) {
        if (existingValue.endsWith(".")) {
            existingValue = existingValue.substring(0, existingValue.length() - 1) + "; " + newValue;
        } else {
            existingValue = existingValue + "; " + newValue;
        }
        return existingValue;
    }

    private static Optional<String> getAnnotationValue(Parameter param, Class<? extends Annotation> annotationClass) {
        Annotation annotation = param.getAnnotation(annotationClass);

        if (Objects.isNull(annotation)) {
            return Optional.empty();
        }

        return extractValueFromAnnotation(annotation, param);
    }

    private static Optional<String> extractValueFromAnnotation(Annotation annotation, Parameter param) {
        return switch (annotation) {
            case RequestParam rp -> Optional.of(resolveName(rp.value(), param));
            case RequestHeader rh -> Optional.of(resolveName(rh.value(), param));
            case PathVariable pv -> Optional.of(resolveName(pv.value(), param));
            case RequestPart rp -> Optional.of(resolveName(rp.value(), param));
            case CookieValue cv -> Optional.of(resolveName(cv.value(), param));
            case MatrixVariable mv -> Optional.of(resolveName(mv.value(), param));
            default -> Optional.empty();
        };
    }

    private static String resolveName(String value, Parameter param) {
        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? param.getName() : trimmedValue;
    }

    private static String getMessageUtils(String messageKey, Object... params) {
        return MessageUtils.getMessage(messageKey, params);
    }
}
