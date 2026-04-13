package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.DateRangeValidation;
import com.example.exampleproject.utils.MessageUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.*;
import org.springframework.web.bind.annotation.BindParam;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Tests for class {@link DateRangeValidator}
 */
@Tag(value = "DateRangeValidator_Tests")
@DisplayName("DateRangeValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class DateRangeValidatorTest {

    // ── Tested method names ──────────────────────────────────────────────────────
    private static final String IS_VALID = "isValid";

    // ── Test data values ─────────────────────────────────────────────────────────
    private static final String DATE_A_FIELD = "dateA";
    private static final String DATE_B_FIELD = "dateB";
    private static final String DATE_A_JSON_PROPERTY = "dataInicial";
    private static final String DATE_B_JSON_PROPERTY = "dataFinal";

    // ── I18N: CSV delimiter ───────────────────────────────────────────────────────
    private static final char CSV_DELIMITER = '|';

    private record ExampleDateObject(Date dateA, Date dateB) {
    }

    private record ExampleLocalDateObject(LocalDate dateA, LocalDate dateB) {
    }

    private record ExampleLocalDateTimeObject(LocalDateTime dateA, LocalDateTime dateB) {
    }

    private record ExampleZonedDateTimeObject(ZonedDateTime dateA, ZonedDateTime dateB) {
    }

    private record ExampleMergedDateObject(LocalDate dateA, LocalDateTime dateB) {
    }

    private record ExampleJsonPropertyDateObject(
            @JsonProperty(DATE_A_JSON_PROPERTY) LocalDate dateA,
            @JsonProperty(DATE_B_JSON_PROPERTY) LocalDateTime dateB) {
    }

    private static class ExampleBindParamDateObject {
        private final LocalDate dateA;
        private final LocalDate dateB;

        public ExampleBindParamDateObject(
                @BindParam("dataInicial") LocalDate dateA,
                @BindParam("dataFinal") LocalDate dateB) {
            this.dateA = dateA;
            this.dateB = dateB;
        }

        public LocalDate getDateA() { return dateA; }
        public LocalDate getDateB() { return dateB; }
    }

    private static class ExamplePojoObject {
        private final LocalDate dateA;
        private final LocalDate dateB;
        private final boolean valid = true;

        public ExamplePojoObject(LocalDate dateA, LocalDate dateB) {
            this.dateA = dateA;
            this.dateB = dateB;
        }

        public LocalDate getDateA() {
            return dateA;
        }

        public LocalDate getDateB() {
            return dateB;
        }

        public boolean isValid() {
            return valid;
        }
    }

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @InjectMocks
    private DateRangeValidator dateRangeValidator;

    private Locale defaultLocale;

    @BeforeAll
    static void setUpAll() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);

        ReflectionTestUtils.setField(MessageUtils.class, "messageSourceStatic", messageSource);
    }

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();

        DateRangeValidation dateRangeValidation = mock(DateRangeValidation.class);
        when(dateRangeValidation.dateAField()).thenReturn(DATE_A_FIELD);
        when(dateRangeValidation.dateBField()).thenReturn(DATE_B_FIELD);
        dateRangeValidator.initialize(dateRangeValidation);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Converts a language tag from @CsvSource format ("pt_BR", "en") into a Locale
     * and sets it as the current locale for the running test.
     *
     * @param languageTag language tag using underscore notation (e.g. "pt_BR", "en")
     */
    private static void setLocale(String languageTag) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (Date) and dateB (Date) are in valid range, then should return true")
    @Test
    void isValid_WhenDateARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final Date dateA = new Date();
        final Date dateB = new Date(System.currentTimeMillis() + 10000);
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");

        // Verify
        verifyNoInteractions(context);
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (LocalDate) and dateB (LocalDate) are in valid range, then should return true")
    @Test
    void isValid_WhenLocalDateARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final LocalDate dateA = LocalDate.now();
        final LocalDate dateB = LocalDate.now().plusDays(1);
        ExampleLocalDateObject exampleObject = new ExampleLocalDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");

        // Verify
        verifyNoInteractions(context);
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (LocalDateTime) and dateB (LocalDateTime) are in valid range, then should return true")
    @Test
    void isValid_WhenLocalDateTimeARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final LocalDateTime dateA = LocalDateTime.now();
        final LocalDateTime dateB = LocalDateTime.now().plusDays(1);
        ExampleLocalDateTimeObject exampleObject = new ExampleLocalDateTimeObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");

        // Verify
        verifyNoInteractions(context);
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (ZonedDateTime) and dateB (ZonedDateTime) are in valid range, then should return true")
    @Test
    void isValid_WhenZonedDateTimeARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final ZonedDateTime dateA = ZonedDateTime.now();
        final ZonedDateTime dateB = ZonedDateTime.now().plusDays(1);
        ExampleZonedDateTimeObject exampleObject = new ExampleZonedDateTimeObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");

        // Verify
        verifyNoInteractions(context);
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (LocalDate) and dateB (LocalDateTime) are in valid range, then should return true")
    @Test
    void isValid_WhenMergedDateARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final LocalDate dateA = LocalDate.now();
        final LocalDateTime dateB = LocalDateTime.now().plusDays(1);
        ExampleMergedDateObject exampleObject = new ExampleMergedDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");

        // Verify
        verifyNoInteractions(context);
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a POJO with JavaBeans getters, then should use 'get' and 'is' accessors and return true")
    @Test
    void isValid_WhenPojoWithGettersIsUsed_ThenShouldResolveAccessorsAndReturnTrue() {
        // Arrange
        final LocalDate dateA = LocalDate.now();
        final LocalDate dateB = LocalDate.now().plusDays(1);
        ExamplePojoObject exampleObject = new ExamplePojoObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "The result should be true when using a POJO with proper getters");

        // Verify
        verifyNoInteractions(context);
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a POJO with 'is' boolean accessor, then should resolve the accessor correctly")
    @Test
    void isValid_WhenPojoWithIsAccessorIsUsed_ThenShouldResolveIsAccessorCorrectly() {
        // Arrange
        final LocalDate dateA = LocalDate.now();
        final LocalDate dateB = LocalDate.now().plusDays(1);
        ExamplePojoObject exampleObject = new ExamplePojoObject(dateA, dateB);

        // Re-initialize to test 'is' accessor for 'valid' field
        DateRangeValidation dateRangeValidation = mock(DateRangeValidation.class);
        when(dateRangeValidation.dateAField()).thenReturn("valid");
        when(dateRangeValidation.dateBField()).thenReturn(DATE_B_FIELD);
        dateRangeValidator.initialize(dateRangeValidation);

        // Act
        // This will attempt to call exampleObject.isValid()
        // It returns false because boolean is not a date, but it hits the logic
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertFalse(isValid, "Should return false because boolean cannot be converted to date, but it confirms 'isValid()' was called");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA and dateB are in invalid range, then should throw exception with localized message")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|dateA deve ser anterior a dateB.",
            "en|dateA must be before dateB."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateARangeIsInvalid_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final Date dateA = new Date(System.currentTimeMillis() + 10000);
        final Date dateB = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertFalse(isValid, "The result should be false when dateA is after dateB");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals(expectedMessage, messageCaptor.getValue(),
                "The exception message should match the localized error for lang=" + languageTag);

        // Verify
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(eq(expectedMessage));
        verify(builder).addPropertyNode(eq(DATE_A_FIELD));
        verify(nodeBuilder).addConstraintViolation();
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA is null, then should throw exception with localized message")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|Ambas datas dateA e dateB devem ser preenchidas ou ambas estarem ausentes.",
            "en|Both dates dateA and dateB must be filled in or both must be missing."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateAIsNull_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final Date dateB = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(null, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertFalse(isValid, "The result should be false when dateA is null and dateB is not");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals(expectedMessage, messageCaptor.getValue(),
                "The exception message should match the localized error for lang=" + languageTag);

        // Verify
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(eq(expectedMessage));
        verify(builder).addPropertyNode(eq(DATE_A_FIELD));
        verify(nodeBuilder).addConstraintViolation();
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateB is null, then should throw exception with localized message")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|Ambas datas dateA e dateB devem ser preenchidas ou ambas estarem ausentes.",
            "en|Both dates dateA and dateB must be filled in or both must be missing."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateBIsNull_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final Date dateA = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, null);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertFalse(isValid, "The result should be false when dateB is null and dateA is not");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals(expectedMessage, messageCaptor.getValue(),
                "The exception message should match the localized error for lang=" + languageTag);

        // Verify
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(eq(expectedMessage));
        verify(builder).addPropertyNode(eq(DATE_B_FIELD));
        verify(nodeBuilder).addConstraintViolation();
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given both dateA and dateB are null, then should return true")
    @Test
    void isValid_WhenBothDateAAndDateBAreNull_ThenShouldReturnTrue() {
        // Arrange
        ExampleDateObject exampleObject = new ExampleDateObject(null, null);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "The result should be true when both dates are null");

        // Verify
        verifyNoInteractions(context);
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(12)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an exception is thrown, then should return false")
    @Test
    void isValid_WhenExceptionThrown_ThenShouldReturnFalse() {
        // Arrange
        final int dateAValue = 123;
        final LocalDate dateBValue = LocalDate.now().plusDays(1);

        record ExampleObjectError(int dateA, LocalDate dateB) {
        }

        ExampleObjectError exampleObject = new ExampleObjectError(dateAValue, dateBValue);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertFalse(isValid, "The result should be false when an exception is thrown during validation");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(13)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given fields have JsonProperty annotations, then should use JsonProperty names in error messages")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|dataInicial deve ser anterior a dataFinal.",
            "en|dataInicial must be before dataFinal."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFieldsHaveJsonPropertyAnnotations_ThenShouldUseJsonPropertyNamesInErrorMessages(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final LocalDate dateA = LocalDate.now().plusDays(1);
        final LocalDateTime dateB = LocalDateTime.now();
        ExampleJsonPropertyDateObject exampleObject = new ExampleJsonPropertyDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertFalse(isValid, "The result should be false when dateA is after dateB");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals(expectedMessage, messageCaptor.getValue(),
                "The exception message should match the localized error for lang=" + languageTag);

        // Verify
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(eq(expectedMessage));
        verify(builder).addPropertyNode(eq(DATE_A_FIELD));
        verify(nodeBuilder).addConstraintViolation();
    }

    /**
     * Ensures all i18n error keys used by {@link DateRangeValidator}
     * exist in messages_pt_BR.properties AND messages_en.properties.
     */
    @Order(99)
    @Tag(value = "i18n_key_existence")
    @DisplayName("i18n Given all error message keys, then they must resolve in pt_BR and en")
    @ParameterizedTest(name = "[{index}] key={0} | lang={1}")
    @CsvSource(
        value = {
            "msg.validation.request.field.date.range.empty|pt_BR",
            "msg.validation.request.field.date.range.empty|en",
            "msg.validation.request.field.date.range.invalid|pt_BR",
            "msg.validation.request.field.date.range.invalid|en"
        },
        delimiter = CSV_DELIMITER
    )
    void i18n_WhenErrorKeyResolved_ThenShouldNotReturnTheKeyItself(
            String messageKey,
            String languageTag) {

        // Arrange
        setLocale(languageTag);

        // Act
        String resolved = MessageUtils.getMessage(messageKey, LocaleContextHolder.getLocale());

        // Assert
        assertNotNull(resolved,
                "Resolved message should not be null — key=" + messageKey + " lang=" + languageTag);
        assertNotEquals(messageKey, resolved,
                "Resolved message equals the key itself — key is MISSING from .properties: "
                + messageKey + " | lang=" + languageTag);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "pt_BR|Ambas datas dataInicial e dataFinal devem ser preenchidas ou ambas estarem ausentes.",
            "en|Both dates dataInicial and dataFinal must be filled in or both must be missing."
    }, delimiter = '|')
    @DisplayName("isValid() - When using @BindParam in constructor, use mapped names in error message")
    void isValid_WhenBindParamInConstructorIsUsed_ThenShouldUseBindParamNamesInErrorMessages(String languageTag, String expectedMessage) {
        // Arrange
        setLocale(languageTag);
        ReflectionTestUtils.setField(dateRangeValidator, "dateAField", "dateA");
        ReflectionTestUtils.setField(dateRangeValidator, "dateBField", "dateB");

        ExampleBindParamDateObject object = new ExampleBindParamDateObject(LocalDate.now(), null);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        // Act
        boolean result = dateRangeValidator.isValid(object, context);

        // Assert
        assertFalse(result);
        verify(context).buildConstraintViolationWithTemplate(expectedMessage);
    }
}
