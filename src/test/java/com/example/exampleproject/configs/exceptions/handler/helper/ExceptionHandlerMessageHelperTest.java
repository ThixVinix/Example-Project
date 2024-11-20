package com.example.exampleproject.configs.exceptions.handler.helper;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link ExceptionHandlerMessageHelper}
 */
@SpringBootTest
@Tag(value = "ExceptionHandlerMessageHelper_Tests")
@DisplayName("ExceptionHandlerMessageHelper Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ExceptionHandlerMessageHelperTest {

    private static final String GET_BAD_REQUEST_MESSAGE = "getBadRequestMessage";

    private static final String GET_NOT_FOUND_MESSAGE = "getNotFoundMessage";

    private static final String GET_METHOD_ALLOWED_MESSAGE = "getMethodNotAllowedMessage";

    private static final String GET_INTERNAL_SERVER_ERROR_MESSAGE = "getInternalServerErrorMessage";

    private static final char CSV_DELIMITER = '|';

    private Locale defaultLocale;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(1)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with MethodArgumentNotValidException")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Mensagem de argumento não válido em português",
            "en_US|Invalid argument message in English"
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMethodArgumentNotValidException(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        FieldError fieldError = new FieldError("target", "field", expectedMessage);
        bindingResult.addError(fieldError);

        MethodParameter methodParameter = Mockito.mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get("field"),
                "Checks if the invalid argument message is returned correctly for the locale " + languageTag);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(2)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with HttpMessageNotReadableException")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Requisição JSON malformada.",
            "en_US|Malformed JSON request."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithHttpMessageNotReadableException(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        HttpInputMessage httpInputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("message", httpInputMessage);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the malformed JSON message is returned correctly for the locale " + languageTag);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(3)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with BusinessException root cause")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Mensagem de exceção de negócio em português",
            "en_US|Business exception message in English"
    }, delimiter = CSV_DELIMITER)
    void getNotReadableMessage_WithBusinessExceptionRootCause(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        BusinessException rootCause = new BusinessException(expectedMessage);
        HttpMessageNotReadableException notReadableException = mock(HttpMessageNotReadableException.class);
        when(notReadableException.getRootCause()).thenReturn(rootCause);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(notReadableException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the business exception message is returned correctly for the locale " + languageTag);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(4)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with MethodArgumentNotValidException without default message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Argumento inválido.",
            "en_US|Invalid argument."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMethodArgumentNotValidException_NoDefaultMessage(String languageTag,
                                                                                   String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);

        class MockTarget {
            @JsonProperty("jsonField")
            private String fielName;
        }

        FieldError fieldError = new FieldError(
                "object",
                "fieldName",
                "rejectedValue",
                false,
                null,
                null,
                null);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getTarget()).thenReturn(new MockTarget());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get("fieldName"),
                "Checks if the invalid argument message without default message is returned correctly " +
                        "for the locale " + languageTag);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(5)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with null root cause message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Requisição JSON malformada.",
            "en_US|Malformed JSON request."
    }, delimiter = CSV_DELIMITER)
    void getNotReadableMessage_WithNullRootCauseMessage(String languageTag, String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        HttpMessageNotReadableException mockException = mock(HttpMessageNotReadableException.class);
        Throwable mockRootCause = mock(Throwable.class);

        when(mockException.getRootCause()).thenReturn(mockRootCause);
        when(mockRootCause.getMessage()).thenReturn(null);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(mockException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the malformed JSON request message is returned correctly " +
                        "for the locale " + languageTag + " when root cause message is null.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(6)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with non-BusinessException root cause")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Requisição JSON malformada.",
            "en_US|Malformed JSON request."
    }, delimiter = CSV_DELIMITER)
    void getNotReadableMessage_WithNonBusinessExceptionRootCause(String languageTag, String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        HttpMessageNotReadableException mockException = mock(HttpMessageNotReadableException.class);
        Throwable mockRootCause = mock(Throwable.class);

        when(mockException.getRootCause()).thenReturn(mockRootCause);
        when(mockRootCause.getMessage()).thenReturn("Some error message");

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(mockException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the malformed JSON request message is returned correctly " +
                        "for the locale " + languageTag + " when root cause is a non-BusinessException.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(7)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with MissingServletRequestParameterException")
    @ParameterizedTest(name = "Test {index} => locale={0} | parameterName={1} | expectedMessage={2}")
    @CsvSource(value = {
            "pt_BR|requiredParam|Parâmetro obrigatório ausente: requiredParam.",
            "en_US|requiredParam|Missing required parameter: requiredParam."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMissingServletRequestParameterException(String languageTag,
                                                                          String parameterName,
                                                                          String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException(parameterName, "String");

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the missing required parameter message is returned correctly " +
                        "for the locale " + languageTag + " with parameter named " + parameterName + ".");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(8)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with MissingRequestHeaderException")
    @ParameterizedTest(name = "Test {index} => locale={0} | headerName={1} | expectedMessage={2}")
    @CsvSource(value = {
            "pt_BR|requiredHeader|Cabeçalho obrigatório ausente: requiredHeader.",
            "en_US|requiredHeader|Missing required header: requiredHeader."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMissingRequestHeaderException(String languageTag,
                                                                String headerName,
                                                                String expectedMessage) throws NoSuchMethodException {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        Method method = getClass().getDeclaredMethod("getBadRequestMessage_WithMissingRequestHeaderException",
                String.class, String.class, String.class);
        MethodParameter methodParameter = new MethodParameter(method, 1);

        MissingRequestHeaderException exception =
                new MissingRequestHeaderException(headerName, methodParameter);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the missing required header message is returned correctly " +
                        "for the locale " + languageTag + " with header named " + headerName + ".");
    }


    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(9)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with MethodArgumentTypeMismatchException")
    @ParameterizedTest(name = "Test {index} => locale={0} | parameter={1} | expectedType={2} | receivedValue={3} | expectedMessage={4}")
    @CsvSource(value = {
            "pt_BR|parameter|ValorRecebido|Não é possível converter o valor ValorRecebido para o tipo requerido String.",
            "en_US|parameter|ReceivedValue|It is not possible to convert the value ReceivedValue to the required type String."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMethodArgumentTypeMismatchException(String languageTag,
                                                                      String parameter,
                                                                      String receivedValue,
                                                                      String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn(parameter);
        doReturn(String.class).when(exception).getRequiredType();
        when(exception.getValue()).thenReturn(receivedValue);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get(parameter),
                "Checks if the conversion error message is returned correctly " +
                        "for the locale " + languageTag + " when trying to convert value " + receivedValue +
                        " for parameter " + parameter + ".");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(10)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with multiple FieldErrors")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedKey={1} | field1Message={2} | field2Message={3}")
    @CsvSource(value = {
            "pt_BR|Mensagem de erro um.|Mensagem de erro dois.",
            "en_US|Error message one.|Error message two."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMultipleFieldErrors(String languageTag, String field1Message, String field2Message) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);

        class MockTarget {
            @JsonProperty("jsonField")
            private String field1;
        }

        FieldError fieldError1 = new FieldError(
                "object",
                "field1",
                "rejectedValue1",
                false,
                null,
                null,
                field1Message);

        FieldError fieldError2 = new FieldError(
                "object",
                "field1",
                "rejectedValue2",
                false,
                null,
                null,
                field2Message);

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getTarget()).thenReturn(new MockTarget());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        String combinedMessage;
        if (field1Message.endsWith(".")) {
            combinedMessage = field1Message.substring(0, field1Message.length() - 1) + "; " + field2Message;
        } else {
            combinedMessage = field1Message + "; " + field2Message;
        }

        assertEquals(combinedMessage, result.get("jsonField"),
                "Checks if the concatenated field error message is returned correctly " +
                        "for the locale " + languageTag);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(11)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with multiple FieldErrors triggering else condition")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedKey={1} | field1Message={2} | field2Message={3}")
    @CsvSource(value = {
            "pt_BR|Mensagem de erro sem ponto|Mensagem de erro dois",
            "en_US|Error message without dot|Error message two"
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMultipleFieldErrors_TriggerElseCondition(String languageTag,
                                                                           String field1Message,
                                                                           String field2Message) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);

        class MockTarget {
            @JsonProperty("jsonField1")
            private String field1;
        }

        FieldError fieldError1 = new FieldError(
                "object",
                "field1",
                "rejectedValue1",
                false,
                null,
                null,
                field1Message);

        FieldError fieldError2 = new FieldError(
                "object",
                "field1",
                "rejectedValue2",
                false,
                null,
                null,
                field2Message);

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getTarget()).thenReturn(new MockTarget());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        String combinedMessage = field1Message + "; " + field2Message;
        assertEquals(combinedMessage, result.get("jsonField1"),
                "Checks if the concatenated field error message (without a dot) is returned correctly " +
                        "for the locale " + languageTag + " with field error messages: "
                        + field1Message + " and " + field2Message + ".");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(12)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with MethodArgumentTypeMismatchException triggering case null")
    @ParameterizedTest(name = "Test {index} => locale={0} | parameter={1} | receivedValue={2} | expectedMessage={3}")
    @CsvSource(value = {
            "pt_BR|parameter|ValorRecebido|Está no formato inválido, valor recebido: ValorRecebido.",
            "en_US|parameter|ReceivedValue|It is in invalid format, received value: ReceivedValue."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMethodArgumentTypeMismatchException_TriggerNullCase(
            String languageTag, String parameter, String receivedValue, String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MethodParameter mockParameter = mock(MethodParameter.class);

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                receivedValue, null, parameter, mockParameter, new IllegalArgumentException());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get(parameter),
                "Checks if the invalid format message is returned correctly " +
                        "for the locale " + languageTag + " with parameter " + parameter +
                        " and received value " + receivedValue + ".");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(13)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with target not null and without JsonProperty fields")
    @ParameterizedTest(name = "Test {index} => locale={0} | field1Message={1}")
    @CsvSource(value = {
            "pt_BR|Mensagem de erro um.",
            "en_US|Error message one."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_TargetNotNull_WithoutJsonPropertyFields(String languageTag, String field1Message) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);

        class MockTarget {
            @SuppressWarnings("unused")
            private String field1;
        }

        FieldError fieldError1 = new FieldError(
                "object",
                "field1",
                "rejectedValue1",
                false,
                null,
                null,
                field1Message);

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getTarget()).thenReturn(new MockTarget());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(field1Message, result.get("field1"),
                "Checks if the field error message is returned correctly with the original field name " +
                        "for the locale " + languageTag);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(14)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with null target")
    @ParameterizedTest(name = "Test {index} => locale={0} | field1Message={1}")
    @CsvSource(value = {
            "pt_BR|Mensagem de erro um.",
            "en_US|Error message one."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithNullTarget(String languageTag, String field1Message) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError(
                "object",
                "field1",
                "rejectedValue1",
                false,
                null,
                null,
                field1Message);

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getTarget()).thenReturn(null);  // `target` será `null`

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(field1Message, result.get("field1"),
                "Checks if the field error message is returned correctly with the original field name " +
                        "for the locale " + languageTag + " when target is null");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(15)
    @Tag(GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with MethodArgumentTypeMismatchException for LocalDate, LocalDateTime, Date, and ZonedDateTime with DateTimeFormat annotation")
    @ParameterizedTest(name = "Test {index} => typeClass={0}, locale={1}, parameter={2}, receivedValue={3}, expectedDatePattern={4}, expectedMessage={5}, annotationType={6}")
    @CsvSource(value = {
            "java.time.LocalDate|pt_BR|data|yyyy-MM-dd|'dd/MM/yyyy'|Deve estar no formato dd/MM/yyyy, valor recebido: yyyy-MM-dd.|RequestParam",
            "java.time.LocalDate|en_US|date|yyyy-MM-dd|'dd/MM/yyyy'|Must be in the format dd/MM/yyyy, received value: yyyy-MM-dd.|RequestParam",
            "java.time.LocalDateTime|pt_BR|dataHora|yyyy-MM-dd|'dd/MM/yyyy HH:mm:ss'|Deve estar no formato dd/MM/yyyy HH:mm:ss, valor recebido: yyyy-MM-dd.|RequestHeader",
            "java.time.LocalDateTime|en_US|dateTime|yyyy-MM-dd|'dd/MM/yyyy HH:mm:ss'|Must be in the format dd/MM/yyyy HH:mm:ss, received value: yyyy-MM-dd.|RequestHeader",
            "java.util.Date|pt_BR|data|yyyy-MM-dd|'dd/MM/yyyy'|Deve estar no formato dd/MM/yyyy, valor recebido: yyyy-MM-dd.|PathVariable",
            "java.util.Date|en_US|date|yyyy-MM-dd|'dd/MM/yyyy'|Must be in the format dd/MM/yyyy, received value: yyyy-MM-dd.|PathVariable",
            "java.time.ZonedDateTime|pt_BR|dataHoraZona|yyyy-MM-dd|'dd/MM/yyyy HH:mm:ss Z'|Deve estar no formato dd/MM/yyyy HH:mm:ss Z, valor recebido: yyyy-MM-dd.|RequestParam",
            "java.time.ZonedDateTime|en_US|zonedDateTime|yyyy-MM-dd|'dd/MM/yyyy HH:mm:ss Z'|Must be in the format dd/MM/yyyy HH:mm:ss Z, received value: yyyy-MM-dd.|RequestParam"
    }, delimiter = '|')
    void getBadRequestMessage_WithMethodArgumentTypeMismatchException_ForDateTypes(String typeClassName,
                                                                                   String languageTag,
                                                                                   String parameter,
                                                                                   String receivedValue,
                                                                                   String expectedDatePattern,
                                                                                   String expectedMessage,
                                                                                   String annotationType) throws ClassNotFoundException {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Method mockMethod = mock(Method.class);
        Parameter mockMethodParameter = mock(Parameter.class);
        DateTimeFormat mockDateTimeFormat = mock(DateTimeFormat.class);

        Mockito.lenient().when(mockMethodParameter.isAnnotationPresent(DateTimeFormat.class)).thenReturn(true);
        Mockito.lenient().when(mockMethodParameter.getAnnotation(DateTimeFormat.class)).thenReturn(mockDateTimeFormat);
        Mockito.lenient().when(mockDateTimeFormat.pattern()).thenReturn(expectedDatePattern);
        when(mockMethod.getParameters()).thenReturn(new Parameter[]{mockMethodParameter});

        mockAnnotation(mockMethodParameter, annotationType, parameter);

        Class<?> typeClass = Class.forName(typeClassName);

        MethodParameter customParameter = new MethodParameter(mockMethod, -1) {
            @Override
            @Nonnull
            public Class<?> getParameterType() {
                return typeClass;
            }
        };

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                receivedValue, typeClass, parameter, customParameter, new IllegalArgumentException());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get(parameter),
                "Checks if the date formatting error message is returned correctly " +
                        "for the type " + typeClassName + " and locale " + languageTag +
                        " with parameter " + parameter + " and received value " + receivedValue +
                        ". Expected date pattern: " + expectedDatePattern + ".");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(16)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with MethodArgumentTypeMismatchException without DateTimeFormat annotation")
    @ParameterizedTest(name = "Test {index} => typeClassName={0} | locale={1} | parameter={2} | receivedValue={3} | expectedMessage={4}")
    @CsvSource(value = {
            "java.time.LocalDate|pt_BR|data|ValorRecebido|Deve estar no formato yyyy-MM-dd, valor recebido: ValorRecebido.",
            "java.time.LocalDate|en_US|date|ReceivedValue|Must be in the format yyyy-MM-dd, received value: ReceivedValue.",
            "java.time.LocalDateTime|pt_BR|dataHora|ValorRecebido|Deve estar no formato yyyy-MM-dd'T'HH:mm:ss, valor recebido: ValorRecebido.",
            "java.time.LocalDateTime|en_US|dateTime|ReceivedValue|Must be in the format yyyy-MM-dd'T'HH:mm:ss, received value: ReceivedValue.",
            "java.time.LocalTime|pt_BR|hora|ValorRecebido|Deve estar no formato HH:mm:ss, valor recebido: ValorRecebido.",
            "java.time.LocalTime|en_US|time|ReceivedValue|Must be in the format HH:mm:ss, received value: ReceivedValue.",
            "java.time.ZonedDateTime|pt_BR|dataHoraZona|ValorRecebido|Deve estar no formato yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z', valor recebido: ValorRecebido.",
            "java.time.ZonedDateTime|en_US|zonedDateTime|ReceivedValue|Must be in the format yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z', received value: ReceivedValue.",
            "java.util.Date|pt_BR|data|ValorRecebido|Está no formato inválido, valor recebido: ValorRecebido.",
            "java.util.Date|en_US|date|ReceivedValue|It is in invalid format, received value: ReceivedValue."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMethodArgumentTypeMismatchException_WithoutDateTimeFormatAnnotation(
            String typeClassName,
            String languageTag,
            String parameter,
            String receivedValue,
            String expectedMessage) throws ClassNotFoundException {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Method mockMethod = mock(Method.class);
        Parameter mockMethodParameter = mock(Parameter.class);

        Class<?> typeClass = Class.forName(typeClassName);

        when(mockMethodParameter.isAnnotationPresent(DateTimeFormat.class)).thenReturn(false);
        when(mockMethodParameter.getType()).thenAnswer(_ -> typeClass);
        when(mockMethod.getParameters()).thenReturn(new Parameter[]{mockMethodParameter});

        RequestParam mockRequestParam = mock(RequestParam.class);
        when(mockRequestParam.value()).thenReturn(parameter);
        when(mockMethodParameter.getAnnotation(RequestParam.class)).thenReturn(mockRequestParam);

        MethodParameter customParameter = new MethodParameter(mockMethod, -1) {
            @Override
            @Nonnull
            public Class<?> getParameterType() {
                return typeClass;
            }
        };

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                receivedValue, typeClass, parameter, customParameter, new IllegalArgumentException());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get(parameter),
                "Checks if the invalid format message is returned correctly " +
                        "for the locale " + languageTag + " with parameter " + parameter +
                        " and received value " + receivedValue + " without DateTimeFormat annotation.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(17)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - where getMethod() returns null")
    @ParameterizedTest(name = "Test {index} => locale={0} | parameter={1} | receivedValue={2} | expectedMessage={3}")
    @CsvSource(value = {
            "pt_BR|data|ValorRecebido|Não é possível converter o valor ValorRecebido para o tipo requerido LocalDate.",
            "en_US|date|ReceivedValue|It is not possible to convert the value ReceivedValue to the required type LocalDate."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithMethodArgumentTypeMismatchException_MethodNull(String languageTag,
                                                                                 String parameter,
                                                                                 String receivedValue,
                                                                                 String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MethodParameter mockMethodParameter = mock(MethodParameter.class);

        when(mockMethodParameter.getMethod()).thenReturn(null);

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                receivedValue, LocalDate.class, parameter, mockMethodParameter, new IllegalArgumentException());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get(parameter),
                "Checks if the invalid format message is returned correctly " +
                        "for the locale " + languageTag + " with parameter " + parameter +
                        " and received value " + receivedValue + " when getMethod() returns null.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(18)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - get expected date format catches exception")
    @ParameterizedTest(name = "Test {index} => locale={0} | parameter={1} | expectedMessage={2}")
    @CsvSource(value = {
            "pt_BR|data|ValorRecebido|Não é possível converter o valor ValorRecebido para o tipo requerido LocalDate.",
            "en_US|date|ReceivedValue|It is not possible to convert the value ReceivedValue to the required type LocalDate."
    }, delimiter = CSV_DELIMITER)
    void getExpectedDateFormat_ForMissingParameter_CatchesException(String languageTag,
                                                                    String parameter,
                                                                    String receivedValue,
                                                                    String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MethodParameter mockMethodParameter = mock(MethodParameter.class);

        when(mockMethodParameter.getMethod()).thenThrow(new RuntimeException("Mocked Exception"));

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                receivedValue, LocalDate.class, parameter, mockMethodParameter, new IllegalArgumentException());

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(exception);

        // Assert
        assertEquals(expectedMessage, result.get(parameter),
                "Checks if the invalid format message is returned correctly " +
                        "for the locale " + languageTag + " with parameter " + parameter +
                        " and received value " + receivedValue + " when getMethod() throws an exception.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(19)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with generic Exception for default case")
    @ParameterizedTest(name = "Test {index} => locale={0} | exceptionMessage={1} | expectedMessage={2}")
    @CsvSource(value = {
            "pt_BR|Mensagem genérica de exceção|Mensagem genérica de exceção",
            "en_US|Generic exception message|Generic exception message"
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithGenericException_ForDefaultCase(String languageTag,
                                                                  String exceptionMessage,
                                                                  String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Exception genericException = new Exception(exceptionMessage);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(genericException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the generic exception message is returned correctly " +
                        "for the locale " + languageTag + " with the exception message: " + exceptionMessage);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(20)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with generic Exception that returns default message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Ocorreu um erro na solicitação enviada, verifique os parâmetros enviados e tente novamente.",
            "en_US|An error occurred in the submitted request, please check the submitted parameters and try again."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithNullExceptionMessage_ForDefaultCase(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Exception genericException = new Exception((String) null);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(genericException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the default message is returned correctly " +
                        "for the locale " + languageTag + " when the exception message is null.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(21)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with null exception")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Ocorreu um erro na solicitação enviada, verifique os parâmetros enviados e tente novamente.",
            "en_US|An error occurred in the submitted request, please check the submitted parameters and try again."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WithNullException(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(null);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the default message is returned correctly " +
                        "for the locale " + languageTag + " when the exception is null.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(22)
    @Tag(GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with ConstraintViolationException")
    @Test
    void getBadRequestMessage_WithConstraintViolationException() {

        // Arrange
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation3 = mock(ConstraintViolation.class);

        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);
        Path path3 = mock(Path.class);

        class RootBean {
            /**
             * This method is intentionally empty because it is used to simulate
             * a method with parameter annotations for testing purposes.
             *
             * @param field1 The first field parameter annotated with @RequestParam.
             * @param field2 The second field parameter annotated with @RequestHeader.
             */
            public void sampleMethod(@RequestParam("field1") String field1, @RequestHeader("field2") String field2) {
                throw new UnsupportedOperationException("This method is not supposed to be called");
            }
        }

        RootBean rootBean = new RootBean();

        when(violation1.getMessage()).thenReturn("Error message 1");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(path1.toString()).thenReturn("sampleMethod.field1");
        doReturn(rootBean).when(violation1).getRootBean();

        when(violation2.getMessage()).thenReturn("Error message 2.1");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(path2.toString()).thenReturn("sampleMethod.field1");
        doReturn(rootBean).when(violation2).getRootBean();

        when(violation3.getMessage()).thenReturn("Error message 2.2");
        when(violation3.getPropertyPath()).thenReturn(path3);
        when(path3.toString()).thenReturn("sampleMethod.field2");
        doReturn(rootBean).when(violation3).getRootBean();

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation1);
        violations.add(violation2);
        violations.add(violation3);

        ConstraintViolationException constraintViolationException = new ConstraintViolationException(violations);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(constraintViolationException);

        // Assert
        String field1ErrorMessage = result.get("field1");
        assertTrue(field1ErrorMessage.contains("Error message 1"),
                "Checks if the field1 message contains 'Error message 1'");
        assertTrue(field1ErrorMessage.contains("Error message 2.1"),
                "Checks if the field1 message contains 'Error message 2.1'");

        assertEquals("Error message 2.2", result.get("field2"),
                "Checks if the correct error message is returned for field2.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(23)
    @Tag(GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with ConstraintViolationException when matchedMethod is null")
    @Test
    void getBadRequestMessage_WhenConstraintViolationLeadsToUnmatchedMethod() {

        // Arrange
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);

        String errorMessage = "Error message for field1";

        when(violation1.getPropertyPath()).thenReturn(path1);
        when(path1.toString()).thenReturn("sampleMethod.field1");
        when(violation1.getMessage()).thenReturn(errorMessage);

        Object rootBean = new Object();
        doReturn(rootBean).when(violation1).getRootBean();

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation1);

        ConstraintViolationException constraintViolationException = new ConstraintViolationException(violations);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(constraintViolationException);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result should contain one entry");
        assertEquals(errorMessage, result.get("field1"),
                "Should return the correct error message for field1");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getBadRequestMessage(Exception)}
     */
    @Order(24)
    @Tag(value = GET_BAD_REQUEST_MESSAGE)
    @DisplayName(GET_BAD_REQUEST_MESSAGE + " - with HandlerMethodValidationException")
    @ParameterizedTest(name = "Test {index} => locale={0}, expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|A requisição enviada contém erro(s) de validação. Por favor, verifique a documentação e tente novamente.",
            "en_US|The request contains validation error(s). Please check the documentation and try again."
    }, delimiter = CSV_DELIMITER)
    void getBadRequestMessage_WhenHandlerMethodValidationException(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        HandlerMethodValidationException handlerMethodValidationException = mock(HandlerMethodValidationException.class);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getBadRequestMessage(handlerMethodValidationException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the correct validation failure message is returned for the locale " + languageTag);
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getNotFoundMessage(Exception)}
     */
    @Order(25)
    @Tag(value = GET_NOT_FOUND_MESSAGE)
    @DisplayName(GET_NOT_FOUND_MESSAGE + " - with NoResourceFoundException")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O recurso solicitado não foi encontrado. Por favor, verifique a URL enviada e tente novamente.",
            "en_US|The requested resource was not found. Please check the submitted URL and try again."
    }, delimiter = CSV_DELIMITER)
    void getNotFoundMessage_WithNoResourceFoundException(String languageTag, String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Exception mockException = mock(NoResourceFoundException.class);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getNotFoundMessage(mockException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the resource not found message is returned correctly " +
                        "for the locale " + languageTag + " when the exception is NoResourceFoundException.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getNotFoundMessage(Exception)}
     */
    @Order(26)
    @Tag(value = GET_NOT_FOUND_MESSAGE)
    @DisplayName(GET_NOT_FOUND_MESSAGE + " - with general exception")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Recurso não encontrado.",
            "en_US|Resource not found."
    }, delimiter = CSV_DELIMITER)
    void getNotFoundMessage_WithGeneralException(String languageTag, String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Exception mockException = mock(Exception.class);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getNotFoundMessage(mockException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the resource not found message is returned correctly " +
                        "for the locale " + languageTag + " when the exception is generic.");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getMethodNotAllowedMessage(HttpRequestMethodNotSupportedException)}
     */
    @Order(27)
    @Tag(value = GET_METHOD_ALLOWED_MESSAGE)
    @DisplayName(GET_METHOD_ALLOWED_MESSAGE + " - with HttpRequestMethodNotSupportedException")
    @ParameterizedTest(name = "Test {index} => method={0} | locale={1} | expectedMessageKey={2}")
    @CsvSource(value = {
            "POST|pt_BR|O método HTTP POST não é suportado para essa URL, por favor verifique a documentação para métodos permitidos.",
            "GET|en_US|The HTTP method GET is not supported for this URL, please check the documentation for allowed methods."
    }, delimiter = CSV_DELIMITER)
    void getMethodNotAllowedMessage_Test(String method, String languageTag, String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        HttpRequestMethodNotSupportedException mockException = new HttpRequestMethodNotSupportedException(method);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getMethodNotAllowedMessage(mockException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the method not allowed message is returned correctly " +
                        "for the HTTP method " + method + " and locale " + languageTag + ".");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getInternalServerErrorMessage(Exception)}
     */
    @Order(28)
    @Tag(value = GET_INTERNAL_SERVER_ERROR_MESSAGE)
    @DisplayName(GET_INTERNAL_SERVER_ERROR_MESSAGE + " - with non-null exception message")
    @ParameterizedTest(name = "Test {index} => locale={0} | exceptionMessage={1}")
    @CsvSource(value = {
            "pt_BR|Esta é uma mensagem de erro.",
            "en_US|This is an error message."
    }, delimiter = CSV_DELIMITER)
    void getInternalServerErrorMessage_WithNonNullExceptionMessage(String languageTag, String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Exception mockException = new Exception(expectedMessage);

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getInternalServerErrorMessage(mockException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the internal server error message is returned correctly " +
                        "for the locale " + languageTag + " with the exception message: " + expectedMessage + ".");
    }

    /**
     * Method test for
     * {@link ExceptionHandlerMessageHelper#getInternalServerErrorMessage(Exception)}
     */
    @Order(29)
    @Tag(value = GET_INTERNAL_SERVER_ERROR_MESSAGE)
    @DisplayName(GET_INTERNAL_SERVER_ERROR_MESSAGE + " - with null exception message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessageKey={1}")
    @CsvSource(value = {
            "pt_BR|Erro inesperado, tente novamente mais tarde.",
            "en_US|Unexpected error, please try again later."
    }, delimiter = CSV_DELIMITER)
    void getInternalServerErrorMessage_WithNullExceptionMessage(String languageTag, String expectedMessage) {

        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Exception mockException = new Exception();

        // Act
        Map<String, String> result = ExceptionHandlerMessageHelper.getInternalServerErrorMessage(mockException);

        // Assert
        assertEquals(expectedMessage, result.get("message"),
                "Checks if the default internal server error message is returned correctly " +
                        "for the locale " + languageTag + " when the exception message is null.");
    }

    /**
     * Mocks the specified annotation on a given method parameter.
     *
     * @param mockMethodParameter the method parameter to mock the annotation on
     * @param annotationType      the type of annotation to mock (e.g., "RequestParam", "RequestHeader", "PathVariable")
     * @param parameter           the value to return for the annotation's parameter
     * @throws UnsupportedOperationException if the specified annotation type is not supported
     */
    private void mockAnnotation(Parameter mockMethodParameter, String annotationType, String parameter) {
        switch (annotationType) {
            case "RequestParam" -> {
                RequestParam mockRequestParam = mock(RequestParam.class);
                Mockito.lenient().when(mockRequestParam.value()).thenReturn(parameter);
                Mockito.lenient().when(mockMethodParameter.getAnnotation(RequestParam.class))
                        .thenReturn(mockRequestParam);
            }
            case "RequestHeader" -> {
                RequestHeader mockRequestHeader = mock(RequestHeader.class);
                Mockito.lenient().when(mockRequestHeader.value()).thenReturn(parameter);
                Mockito.lenient().when(mockMethodParameter.getAnnotation(RequestHeader.class))
                        .thenReturn(mockRequestHeader);
            }
            case "PathVariable" -> {
                PathVariable mockPathVariable = mock(PathVariable.class);
                Mockito.lenient().when(mockPathVariable.value()).thenReturn(parameter);
                Mockito.lenient().when(mockMethodParameter.getAnnotation(PathVariable.class))
                        .thenReturn(mockPathVariable);
            }
            default -> throw new UnsupportedOperationException("Unsupported annotation type: " + annotationType);
        }
    }
}