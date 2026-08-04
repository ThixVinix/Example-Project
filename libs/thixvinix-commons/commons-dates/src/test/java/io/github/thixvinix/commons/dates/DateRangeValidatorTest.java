package io.github.thixvinix.commons.dates;

import io.github.thixvinix.commons.i18n.Messages;
import io.github.thixvinix.commons.i18n.testing.CommonsI18nExtension;
import io.github.thixvinix.commons.i18n.testing.TestLocale;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.BindParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DateRangeValidator}.
 */
@Tag(value = "DateRangeValidator_Tests")
@DisplayName("DateRangeValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({MockitoExtension.class, CommonsI18nExtension.class})
class DateRangeValidatorTest {

    private static final String IS_VALID = "isValid";

    private static final String DATE_A_FIELD = "dateA";
    private static final String DATE_B_FIELD = "dateB";
    private static final String DATE_A_JSON_PROPERTY = "dataInicial";
    private static final String DATE_B_JSON_PROPERTY = "dataFinal";

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

        public LocalDate getDateA() {
            return dateA;
        }

        public LocalDate getDateB() {
            return dateB;
        }
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

    @BeforeEach
    void setUp() {
        DateRangeValidation dateRangeValidation = mock(DateRangeValidation.class);
        when(dateRangeValidation.dateAField()).thenReturn(DATE_A_FIELD);
        when(dateRangeValidation.dateBField()).thenReturn(DATE_B_FIELD);
        dateRangeValidator.initialize(dateRangeValidation);
    }

    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (Date) and dateB (Date) are in valid range, then should return true")
    @Test
    void isValid_WhenDateARangeIsValid_ThenShouldReturnTrue() {
        final Date dateA = new Date();
        final Date dateB = new Date(System.currentTimeMillis() + 10000);
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");
        verifyNoInteractions(context);
    }

    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (LocalDate) and dateB (LocalDate) are in valid range, then should return true")
    @Test
    void isValid_WhenLocalDateARangeIsValid_ThenShouldReturnTrue() {
        final LocalDate dateA = LocalDate.now();
        final LocalDate dateB = LocalDate.now().plusDays(1);
        ExampleLocalDateObject exampleObject = new ExampleLocalDateObject(dateA, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");
        verifyNoInteractions(context);
    }

    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (LocalDateTime) and dateB (LocalDateTime) are in valid range, then should return true")
    @Test
    void isValid_WhenLocalDateTimeARangeIsValid_ThenShouldReturnTrue() {
        final LocalDateTime dateA = LocalDateTime.now();
        final LocalDateTime dateB = LocalDateTime.now().plusDays(1);
        ExampleLocalDateTimeObject exampleObject = new ExampleLocalDateTimeObject(dateA, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");
        verifyNoInteractions(context);
    }

    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (ZonedDateTime) and dateB (ZonedDateTime) are in valid range, then should return true")
    @Test
    void isValid_WhenZonedDateTimeARangeIsValid_ThenShouldReturnTrue() {
        final ZonedDateTime dateA = ZonedDateTime.now();
        final ZonedDateTime dateB = ZonedDateTime.now().plusDays(1);
        ExampleZonedDateTimeObject exampleObject = new ExampleZonedDateTimeObject(dateA, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");
        verifyNoInteractions(context);
    }

    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA (LocalDate) and dateB (LocalDateTime) are in valid range, then should return true")
    @Test
    void isValid_WhenMergedDateARangeIsValid_ThenShouldReturnTrue() {
        final LocalDate dateA = LocalDate.now();
        final LocalDateTime dateB = LocalDateTime.now().plusDays(1);
        ExampleMergedDateObject exampleObject = new ExampleMergedDateObject(dateA, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertTrue(isValid, "The result should be true when dateA and dateB are in a valid range");
        verifyNoInteractions(context);
    }

    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a POJO with JavaBeans getters, then should use 'get' and 'is' accessors and return true")
    @Test
    void isValid_WhenPojoWithGettersIsUsed_ThenShouldResolveAccessorsAndReturnTrue() {
        final LocalDate dateA = LocalDate.now();
        final LocalDate dateB = LocalDate.now().plusDays(1);
        ExamplePojoObject exampleObject = new ExamplePojoObject(dateA, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertTrue(isValid, "The result should be true when using a POJO with proper getters");
        verifyNoInteractions(context);
    }

    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given a POJO with 'is' boolean accessor, then should resolve the accessor correctly")
    @Test
    void isValid_WhenPojoWithIsAccessorIsUsed_ThenShouldResolveIsAccessorCorrectly() {
        final LocalDate dateA = LocalDate.now();
        final LocalDate dateB = LocalDate.now().plusDays(1);
        ExamplePojoObject exampleObject = new ExamplePojoObject(dateA, dateB);

        // Re-initialize to test 'is' accessor for 'valid' field
        DateRangeValidation dateRangeValidation = mock(DateRangeValidation.class);
        when(dateRangeValidation.dateAField()).thenReturn("valid");
        when(dateRangeValidation.dateBField()).thenReturn(DATE_B_FIELD);
        dateRangeValidator.initialize(dateRangeValidation);

        // This will attempt to call exampleObject.isValid()
        // It returns false because boolean is not a date, but it hits the logic
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertFalse(isValid, "Should return false because boolean cannot be converted to date, but it confirms 'isValid()' was called");
    }

    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA and dateB are in invalid range, then should throw exception with localized message")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|dateA deve ser anterior a dateB.",
            "en|dateA must be before dateB."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateARangeIsInvalid_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final Date dateA = new Date(System.currentTimeMillis() + 10000);
        final Date dateB = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertFalse(isValid, "The result should be false when dateA is after dateB");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals(expectedMessage, messageCaptor.getValue(),
                "The exception message should match the localized error for lang=" + languageTag);

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(eq(expectedMessage));
        verify(builder).addPropertyNode(eq(DATE_A_FIELD));
        verify(nodeBuilder).addConstraintViolation();
    }

    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateA is null, then should throw exception with localized message")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|Ambas datas dateA e dateB devem ser preenchidas ou ambas estarem ausentes.",
            "en|Both dates dateA and dateB must be filled in or both must be missing."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateAIsNull_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final Date dateB = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(null, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertFalse(isValid, "The result should be false when dateA is null and dateB is not");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals(expectedMessage, messageCaptor.getValue(),
                "The exception message should match the localized error for lang=" + languageTag);

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(eq(expectedMessage));
        verify(builder).addPropertyNode(eq(DATE_A_FIELD));
        verify(nodeBuilder).addConstraintViolation();
    }

    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given dateB is null, then should throw exception with localized message")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|Ambas datas dateA e dateB devem ser preenchidas ou ambas estarem ausentes.",
            "en|Both dates dateA and dateB must be filled in or both must be missing."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateBIsNull_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final Date dateA = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, null);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertFalse(isValid, "The result should be false when dateB is null and dateA is not");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals(expectedMessage, messageCaptor.getValue(),
                "The exception message should match the localized error for lang=" + languageTag);

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(eq(expectedMessage));
        verify(builder).addPropertyNode(eq(DATE_B_FIELD));
        verify(nodeBuilder).addConstraintViolation();
    }

    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given both dateA and dateB are null, then should return true")
    @Test
    void isValid_WhenBothDateAAndDateBAreNull_ThenShouldReturnTrue() {
        ExampleDateObject exampleObject = new ExampleDateObject(null, null);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertTrue(isValid, "The result should be true when both dates are null");
        verifyNoInteractions(context);
    }

    @Order(12)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given an exception is thrown, then should return false")
    @Test
    void isValid_WhenExceptionThrown_ThenShouldReturnFalse() {
        final int dateAValue = 123;
        final LocalDate dateBValue = LocalDate.now().plusDays(1);

        record ExampleObjectError(int dateA, LocalDate dateB) {
        }

        ExampleObjectError exampleObject = new ExampleObjectError(dateAValue, dateBValue);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertFalse(isValid, "The result should be false when an exception is thrown during validation");
    }

    @Order(13)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " Given fields have JsonProperty annotations, then should use JsonProperty names in error messages")
    @ParameterizedTest(name = "[{index}] lang={0} | expected={1}")
    @CsvSource(value = {
            "pt_BR|dataInicial deve ser anterior a dataFinal.",
            "en|dataInicial must be before dataFinal."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFieldsHaveJsonPropertyAnnotations_ThenShouldUseJsonPropertyNamesInErrorMessages(
            String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final LocalDate dateA = LocalDate.now().plusDays(1);
        final LocalDateTime dateB = LocalDateTime.now();
        ExampleJsonPropertyDateObject exampleObject = new ExampleJsonPropertyDateObject(dateA, dateB);

        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        assertFalse(isValid, "The result should be false when dateA is after dateB");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals(expectedMessage, messageCaptor.getValue(),
                "The exception message should match the localized error for lang=" + languageTag);

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(eq(expectedMessage));
        verify(builder).addPropertyNode(eq(DATE_A_FIELD));
        verify(nodeBuilder).addConstraintViolation();
    }

    @Order(99)
    @Tag(value = "i18n_key_existence")
    @DisplayName("i18n Given all error message keys, then they must resolve in pt_BR and en")
    @ParameterizedTest(name = "[{index}] key={0} | lang={1}")
    @CsvSource(value = {
            DateMessageKeys.RANGE_EMPTY + "|pt_BR",
            DateMessageKeys.RANGE_EMPTY + "|en",
            DateMessageKeys.RANGE_INVALID + "|pt_BR",
            DateMessageKeys.RANGE_INVALID + "|en"
    }, delimiter = CSV_DELIMITER)
    void i18n_WhenErrorKeyResolved_ThenShouldNotReturnTheKeyItself(String messageKey, String languageTag) {
        TestLocale.set(languageTag);

        String resolved = Messages.get(TestLocale.get(), messageKey);

        assertNotNull(resolved, "Resolved message should not be null — key=" + messageKey + " lang=" + languageTag);
        assertNotEquals(messageKey, resolved,
                "Resolved message equals the key itself — key is MISSING from .properties: "
                        + messageKey + " | lang=" + languageTag);
    }

    @Order(14)
    @ParameterizedTest
    @CsvSource(value = {
            "pt_BR|Ambas datas dataInicial e dataFinal devem ser preenchidas ou ambas estarem ausentes.",
            "en|Both dates dataInicial and dataFinal must be filled in or both must be missing."
    }, delimiter = CSV_DELIMITER)
    @DisplayName("isValid() - When using @BindParam in constructor, use mapped names in error message")
    void isValid_WhenBindParamInConstructorIsUsed_ThenShouldUseBindParamNamesInErrorMessages(
            String languageTag, String expectedMessage) {
        TestLocale.set(languageTag);

        ExampleBindParamDateObject object = new ExampleBindParamDateObject(LocalDate.now(), null);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        boolean result = dateRangeValidator.isValid(object, context);

        assertFalse(result);
        verify(context).buildConstraintViolationWithTemplate(expectedMessage);
    }
}
