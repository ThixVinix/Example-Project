package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.MessageConfig;
import com.example.exampleproject.configs.annotations.DateRangeValidation;
import com.example.exampleproject.utils.MessageUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

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
@SpringBootTest(classes = {MessageConfig.class, MessageUtils.class})
@Tag("DateRangeValidator_Tests")
@DisplayName("DateRangeValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class DateRangeValidatorTest {

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
            @JsonProperty("dataInicial") LocalDate dateA, 
            @JsonProperty("dataFinal") LocalDateTime dateB) {
    }

    private static final char CSV_DELIMITER = '|';

    private static final String IS_VALID = "isValid";

    private Locale defaultLocale;

    private ConstraintValidatorContext context;

    @InjectMocks
    private DateRangeValidator dateRangeValidator;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
        DateRangeValidation dateRangeValidation = mock(DateRangeValidation.class);
        when(dateRangeValidation.dateAField()).thenReturn("dateA");
        when(dateRangeValidation.dateBField()).thenReturn("dateB");
        dateRangeValidator.initialize(dateRangeValidation);

    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given dateA (Date) and dateB (Date) are in valid range, then should return true")
    @Test
    void isValid_WhenDateARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final Date dateA = new Date();
        final Date dateB = new Date(System.currentTimeMillis() + 10000);
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "isValid should return true when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given dateA (LocalDate) and dateB (LocalDate) are in valid range, then should " +
            "return true")
    @Test
    void isValid_WhenLocalDateARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final LocalDate dateA = LocalDate.now();
        final LocalDate dateB = LocalDate.now().plusDays(1);
        ExampleLocalDateObject exampleObject = new ExampleLocalDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "isValid should return true when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given dateA (LocalDateTime) and dateB (LocalDateTime) are in valid range, then " +
            "should return true")
    @Test
    void isValid_WhenLocalDateTimeARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final LocalDateTime dateA = LocalDateTime.now();
        final LocalDateTime dateB = LocalDateTime.now().plusDays(1);
        ExampleLocalDateTimeObject exampleObject = new ExampleLocalDateTimeObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "isValid should return true when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given dateA (ZonedDateTime) and dateB (ZonedDateTime) are in valid range, then " +
            "should return true")
    @Test
    void isValid_WhenZonedDateTimeARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final ZonedDateTime dateA = ZonedDateTime.now();
        final ZonedDateTime dateB = ZonedDateTime.now().plusDays(1);
        ExampleZonedDateTimeObject exampleObject = new ExampleZonedDateTimeObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "isValid should return true when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given dateA (LocalDate) and dateB (LocalDateTime) are in valid range, then " +
            "should return true")
    @Test
    void isValid_WhenMergedDateARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final LocalDate dateA = LocalDate.now();
        final LocalDateTime dateB = LocalDateTime.now().plusDays(1);
        ExampleMergedDateObject exampleObject = new ExampleMergedDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "isValid should return true when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given dateA and dateB are in invalid range, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|dateA deve ser anterior a dateB.",
            "en_US|dateA must be before dateB."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateARangeIsInvalid_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        var nodeBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(context.buildConstraintViolationWithTemplate(MessageUtils.getMessage(
                "msg.validation.request.field.date.range.invalid", "dateA", "dateB"))).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);


        final Date dateA = new Date(System.currentTimeMillis() + 10000);
        final Date dateB = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given dateA is null, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Ambas datas dateA e dateB devem ser preenchidas ou ambas estarem ausentes.",
            "en_US|Both dates dateA and dateB must be filled in or both must be missing."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateAIsNull_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        var nodeBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(context.buildConstraintViolationWithTemplate(MessageUtils.getMessage(
                "msg.validation.request.field.date.range.empty", "dateA", "dateB"))).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final Date dateB = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(null, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false when dateA is null");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given dateB is null, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Ambas datas dateA e dateB devem ser preenchidas ou ambas estarem ausentes.",
            "en_US|Both dates dateA and dateB must be filled in or both must be missing."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDateBIsNull_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        var nodeBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(context.buildConstraintViolationWithTemplate(MessageUtils.getMessage(
                "msg.validation.request.field.date.range.empty", "dateA", "dateB"))).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final Date dateA = new Date();
        ExampleDateObject exampleObject = new ExampleDateObject(dateA, null);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false when dateB is null");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given both dateA and dateB are null, then should return true")
    @Test
    void isValid_WhenBothDateAAndDateBAreNull_ThenShouldReturnTrue() {

        // Arrange
        ExampleDateObject exampleObject = new ExampleDateObject(null, null);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertTrue(isValid, "isValid should return true");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an exception is thrown, then should return false and log error message")
    @Test
    void isValid_WhenExceptionThrown_ThenShouldReturnFalse() {
        // Arrange
        final int dateA = 123;
        final LocalDate dateB = LocalDate.now().plusDays(1);

        record ExampleObjectError(int dateA, LocalDate dateB) {
        }

        ExampleObjectError exampleObject = new ExampleObjectError(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        // Assert
        assertFalse(isValid, "isValid should return false when an exception is thrown");
    }

    /**
     * Method test for
     * {@link DateRangeValidator#isValid(Object, ConstraintValidatorContext)}
     */
    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given fields have JsonProperty annotations, then should use JsonProperty names in error messages")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|dataInicial deve ser anterior a dataFinal.",
            "en_US|dataInicial must be before dataFinal."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFieldsHaveJsonPropertyAnnotations_ThenShouldUseJsonPropertyNamesInErrorMessages(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        var nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        final LocalDate dateA = LocalDate.now().plusDays(1);
        final LocalDateTime dateB = LocalDateTime.now();
        ExampleJsonPropertyDateObject exampleObject = new ExampleJsonPropertyDateObject(dateA, dateB);

        // Act
        boolean isValid = dateRangeValidator.isValid(exampleObject, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false");
    }
}
