package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.MessageConfig;
import com.example.exampleproject.configs.annotations.EnumValueValidation;
import com.example.exampleproject.utils.MessageUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link EnumValueValidator}
 */
@SpringBootTest(classes = {MessageConfig.class, MessageUtils.class})
@Tag(value = "EnumValueValidator_Tests")
@DisplayName("EnumValueValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class EnumValueValidatorTest {

    /**
     * Test enum with getValue method for testing EnumValueValidator
     */
    public enum TestValueEnum {
        VALUE_ONE("one"),
        VALUE_TWO("two"),
        VALUE_THREE("three");

        private final String value;

        TestValueEnum(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public String getValue() {
            return value;
        }
    }

    /**
     * Test enum without getValue method for testing fallback to the enum name
     */
    private enum TestNameEnum {
        NAME_ONE,
        NAME_TWO,
        NAME_THREE
    }

    private static final char CSV_DELIMITER = '|';
    private static final String IS_VALID = "isValid";

    private Locale defaultLocale;

    private ConstraintValidatorContext context;

    @InjectMocks
    private EnumValueValidator enumValueValidator;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link EnumValueValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Parameterized Tests for Valid Cases")
    @ParameterizedTest(name = "Given value={0}, should return true")
    @ValueSource(strings = {"one", "ONE"})
    @NullSource
    void isValid_ValidCases_ShouldReturnTrue(String inputValue) {
        // Arrange
        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(_ -> TestValueEnum.class);

        enumValueValidator.initialize(enumValueValidation);

        // Act
        boolean isValid = enumValueValidator.isValid(inputValue, context);

        // Assert
        assertTrue(isValid, String.format("isValid should return true for the input value: '%s'", inputValue));
    }


    /**
     * Method test for
     * {@link EnumValueValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid enum value, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O valor invalid é inválido. Atualmente, os valores válidos são: one, three, two.",
            "en_US|The value invalid is invalid. Currently, the valid values are: one, three, two."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidEnumValue_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(_ -> TestValueEnum.class);
        enumValueValidator.initialize(enumValueValidation);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = enumValueValidator.isValid("invalid", context);

        // Capture the message
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for an invalid enum value");
    }

    /**
     * Method test for
     * {@link EnumValueValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid enum name when getValue is not available, then should return true")
    @Test
    void isValid_WhenValidEnumNameAndGetValueNotAvailable_ThenShouldReturnTrue() {
        // Arrange
        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(_ -> TestNameEnum.class);
        enumValueValidator.initialize(enumValueValidation);

        // Act
        boolean isValid = enumValueValidator.isValid("NAME_ONE", context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid enum name when getValue is not available");
    }

    /**
     * Method test for
     * {@link EnumValueValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid enum name when getValue is not available, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O valor INVALID é inválido. Atualmente, os valores válidos são: NAME_ONE, NAME_THREE, NAME_TWO.",
            "en_US|The value INVALID is invalid. Currently, the valid values are: NAME_ONE, NAME_THREE, NAME_TWO."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidEnumNameAndGetValueNotAvailable_ThenShouldReturnFalse(String languageTag,
                                                                                  String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(_ -> TestNameEnum.class);

        EnumValueValidator localValidator = new EnumValueValidator();
        localValidator.initialize(enumValueValidation);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = localValidator.isValid("INVALID", context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid,
                "isValid should return false for an invalid enum name when getValue is not available");
    }

    /**
     * Method test for
     * {@link EnumValueValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given hideValidOptions=true, then should hide valid values in error message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O valor invalid é inválido.",
            "en_US|The value invalid is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenHideValidOptionsTrue_ThenShouldHideValidValues(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        EnumValueValidation enumValueValidation = mock(EnumValueValidation.class);
        when(enumValueValidation.enumClass()).thenAnswer(_ -> TestValueEnum.class);
        when(enumValueValidation.hideValidOptions()).thenReturn(true);

        EnumValueValidator localValidator = new EnumValueValidator();
        localValidator.initialize(enumValueValidation);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = localValidator.isValid("invalid", context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(capturedMessage.contains("valid values are"));
        assertFalse(capturedMessage.contains("one, three, two"));
        assertFalse(isValid, "isValid should return false for an invalid enum value");
    }
}
