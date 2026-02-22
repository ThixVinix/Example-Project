package com.example.exampleproject.configs.exceptions.handler.helper;

import com.example.exampleproject.utils.MessageUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Method test for {@link ParameterValidationMessageHelper}
 */
@Tag(value = "ParameterValidationMessageHelper_Tests")
@DisplayName("ParameterValidationMessageHelper Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ParameterValidationMessageHelperTest {

    private static final String GET_MISMATCH_MESSAGE = "getMismatchMessage";
    private static final char CSV_DELIMITER = '|';

    private Locale defaultLocale;

    @BeforeAll
    static void setUpAll() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        ReflectionTestUtils.setField(MessageUtils.class, "messageSourceStatic", messageSource);
    }

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
     * {@link ParameterValidationMessageHelper#getMismatchMessage(MethodArgumentTypeMismatchException)}
     */
    @Order(1)
    @Tag(value = GET_MISMATCH_MESSAGE)
    @DisplayName(GET_MISMATCH_MESSAGE + " Given type mismatch, then should return localized message")
    @ParameterizedTest(name = "Test {index} => lang={0} | expected={1}")
    @CsvSource(value = {
        "pt_BR|Não é possível converter o valor invalid para o tipo requerido Integer.",
        "en_US|It is not possible to convert the value invalid to the required type Integer."
    }, delimiter = CSV_DELIMITER)
    void getMismatchMessage_WhenTypeMismatch_ThenShouldReturnLocalizedMessage(String languageTag, String expectedMessage) {
        // Arrange
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));
        
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "invalid", Integer.class, "testParam", null, new RuntimeException());

        // Act
        Map<String, String> result = ParameterValidationMessageHelper.getMismatchMessage(ex);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertTrue(result.containsKey("testParam"), "The result should contain the parameter name as key");
        assertEquals(expectedMessage, result.get("testParam"), "The error message should match the expected localized message");
    }

    /**
     * Method test for
     * {@link ParameterValidationMessageHelper#getConstraintViolationMessage(jakarta.validation.ConstraintViolationException)}
     */
    @Order(2)
    @Tag(value = "getConstraintViolationMessage")
    @DisplayName("getConstraintViolationMessage Given constraint violation, then should return message")
    @org.junit.jupiter.api.Test
    void getConstraintViolationMessage_WhenConstraintViolation_ThenShouldReturnMessage() {
        // Arrange
        jakarta.validation.ConstraintViolation<?> violation = org.mockito.Mockito.mock(jakarta.validation.ConstraintViolation.class);
        org.hibernate.validator.internal.engine.path.PathImpl path = org.hibernate.validator.internal.engine.path.PathImpl.createPathFromString("method.fieldName");
        
        org.mockito.Mockito.doReturn(path).when(violation).getPropertyPath();
        org.mockito.Mockito.doReturn("Invalid value").when(violation).getMessage();
        
        jakarta.validation.ConstraintViolationException ex = new jakarta.validation.ConstraintViolationException(java.util.Set.of(violation));

        // Act
        Map<String, String> result = ParameterValidationMessageHelper.getConstraintViolationMessage(ex);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals("Invalid value", result.get("fieldName"), "The error message should match");
    }
}
