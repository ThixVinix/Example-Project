package com.example.exampleproject.utils;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Tag(value = "DateUtils_Tests")
@DisplayName("DateUtils Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DateUtilsTest {

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

    private static final char CSV_DELIMITER = '|';

    private static final String CHECK_DATE_RANGE = "checkDateRange";

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
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(1)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given dateA (Date) and dateB (Date) are in valid range, then should does " +
            "not throw")
    @Test
    void isValid_WhenDateARangeIsValid_ThenShouldReturnTrue() {
        // Arrange
        final Date dateA = new Date();
        final Date dateB = new Date(System.currentTimeMillis() + 10000);
        DateUtilsTest.ExampleDateObject exampleObject = new DateUtilsTest.ExampleDateObject(dateA, dateB);

        // Act and Assert
        assertDoesNotThrow(() ->
                        DateUtils.checkDateRange(
                                exampleObject.dateA, "dateA", exampleObject.dateB, "dateB"),
                "checkDateRange should does not throw when dateA and dateB are in valid range");

    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(2)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given dateA (LocalDate) and dateB (LocalDate) are in valid range, then " +
            "should does not throw")
    @Test
    void checkDateRange_WhenLocalDateARangeIsValid_ThenShouldDoesNotThrow() {
        // Arrange
        final LocalDate dateA = LocalDate.now();
        final LocalDate dateB = LocalDate.now().plusDays(1);
        DateUtilsTest.ExampleLocalDateObject exampleObject = new DateUtilsTest.ExampleLocalDateObject(dateA, dateB);

        // Act and Assert
        assertDoesNotThrow(() ->
                        DateUtils.checkDateRange(
                                exampleObject.dateA, "dateA", exampleObject.dateB, "dateB"),
                "checkDateRange should does not throw when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(3)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given dateA (LocalDateTime) and dateB (LocalDateTime) are in valid range, then " +
            "should does not throw")
    @Test
    void checkDateRange_WhenLocalDateTimeARangeIsValid_ThenShouldDoesNotThrow() {
        // Arrange
        final LocalDateTime dateA = LocalDateTime.now();
        final LocalDateTime dateB = LocalDateTime.now().plusDays(1);
        DateUtilsTest.ExampleLocalDateTimeObject exampleObject = new DateUtilsTest.ExampleLocalDateTimeObject(dateA, dateB);

        // Act and Assert
        assertDoesNotThrow(() ->
                        DateUtils.checkDateRange(exampleObject.dateA, "dateA", exampleObject.dateB, "dateB"),
                "checkDateRange should does not throw when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(4)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given dateA (ZonedDateTime) and dateB (ZonedDateTime) are in valid range, then " +
            "should does not throw")
    @Test
    void checkDateRange_WhenZonedDateTimeARangeIsValid_ThenShouldDoesNotThrow() {
        // Arrange
        final ZonedDateTime dateA = ZonedDateTime.now();
        final ZonedDateTime dateB = ZonedDateTime.now().plusDays(1);
        DateUtilsTest.ExampleZonedDateTimeObject exampleObject = new DateUtilsTest.ExampleZonedDateTimeObject(dateA, dateB);

        // Act and Assert
        assertDoesNotThrow(() ->
                        DateUtils.checkDateRange(exampleObject.dateA, "dateA", exampleObject.dateB, "dateB"),
                "checkDateRange should does not throw when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(5)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given dateA (LocalDate) and dateB (LocalDateTime) are in valid range, then " +
            "should does not throw")
    @Test
    void checkDateRange_WhenMergedDateARangeIsValid_ThenShouldDoesNotThrow() {
        // Arrange
        final LocalDate dateA = LocalDate.now();
        final LocalDateTime dateB = LocalDateTime.now().plusDays(1);
        DateUtilsTest.ExampleMergedDateObject exampleObject = new DateUtilsTest.ExampleMergedDateObject(dateA, dateB);

        // Act and Assert
        assertDoesNotThrow(() ->
                        DateUtils.checkDateRange(exampleObject.dateA, "dateA", exampleObject.dateB, "dateB"),
                "checkDateRange should does not throw when dateA and dateB are in valid range");
    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(6)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given dateA and dateB are in invalid range, then should does " +
            "throw BusinessException")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1} | dateAName={2} | dateBName={3}")
    @CsvSource(value = {
            "pt_BR|dateA deve ser anterior a dateB.|dateA|dateB",
            "en_US|dateA must be before dateB.|dateA|dateB"
    }, delimiter = CSV_DELIMITER)
    void checkDateRange_WhenDateARangeIsInvalid_ThenShouldThrowBusinessException(String languageTag,
                                                                          String expectedMessage,
                                                                          String dateAName,
                                                                          String dateBName) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        final Date dateA = new Date(System.currentTimeMillis() + 10000);
        final Date dateB = new Date();

        // Act
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> DateUtils.checkDateRange(dateA, dateAName, dateB, dateBName),
                        "Should throw BusinessException.");

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(7)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given dateA is null, then should does throw BusinessException")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1} | dateAName={2} | dateBName={3}")
    @CsvSource(value = {
            "pt_BR|Ambas datas dateA e dateB devem ser preenchidas ou ambas estarem ausentes.|dateA|dateB",
            "en_US|Both dates dateA and dateB must be filled in or both must be missing.|dateA|dateB"
    }, delimiter = CSV_DELIMITER)
    void checkDateRange_WhenDateAIsNull_ThenShouldThrowBusinessException(String languageTag,
                                                       String expectedMessage,
                                                       String dateAName,
                                                       String dateBName) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        final Date dateB = new Date();

        // Act
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> DateUtils.checkDateRange(null, dateAName, dateB, dateBName),
                        "Should throw BusinessException.");

        // Assert
        assertEquals(expectedMessage, exception.getMessage());

    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(8)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given dateB is null, then should does throw BusinessException")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1} | dateAName={2} | dateBName={3}")
    @CsvSource(value = {
            "pt_BR|Ambas datas dateA e dateB devem ser preenchidas ou ambas estarem ausentes.|dateA|dateB",
            "en_US|Both dates dateA and dateB must be filled in or both must be missing.|dateA|dateB"
    }, delimiter = CSV_DELIMITER)
    void checkDateRange_WhenDateBIsNull_ThenShouldThrowBusinessException(String languageTag,
                                                       String expectedMessage,
                                                       String dateAName,
                                                       String dateBName) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        final Date dateA = new Date();

        // Act
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> DateUtils.checkDateRange(dateA, dateAName, null, dateBName),
                        "Should throw BusinessException.");

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(9)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given both dateA and dateB are null, then should does not throw")
    @Test
    void checkDateRange_WhenBothDateAAndDateBAreNull_ThenShouldDoesNotThrow() {

        // Act and Assert
        assertDoesNotThrow(() ->
                        DateUtils.checkDateRange(null, "dateA", null, "dateB"),
                "checkDateRange should does not throw when dateA and dateB are null");
    }

    /**
     * Method test for
     * {@link DateUtils#checkDateRange(Object, String, Object, String)}
     */
    @Order(10)
    @Tag(value = CHECK_DATE_RANGE)
    @DisplayName(CHECK_DATE_RANGE + " - Given an exception is thrown, then should log error message")
    @Test
    void checkDateRange_WhenExceptionThrown_ThenShouldLogErrorMessage() {
        // Arrange
        final int dateA = 123;
        final LocalDate dateB = LocalDate.now().plusDays(1);

        // Act
        BusinessException exception =
                assertThrows(BusinessException.class,
                        () -> DateUtils.checkDateRange(dateA, "dateA", dateB, "dateB"),
                        "Should throw BusinessException.");

        assertEquals("Unsupported date type: " + dateA, exception.getMessage());

    }

}