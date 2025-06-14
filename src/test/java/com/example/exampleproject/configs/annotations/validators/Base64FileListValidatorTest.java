package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link Base64FileListValidator}
 */
@SpringBootTest
@Tag("Base64FileListValidator_Tests")
@DisplayName("Base64FileListValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class Base64FileListValidatorTest {

    private static final char CSV_DELIMITER = '|';
    private static final String IS_VALID = "isValid";
    private static final String INITIALIZE = "initialize";
    private static final String VALID_PDF_MIME_TYPE = "application/pdf";
    private static final String VALID_JPEG_MIME_TYPE = "image/jpeg";

    // Small base64 encoded PDF (just a few bytes)
    private static final String VALID_SMALL_PDF = "data:application/pdf;base64,JVBERi0xLjAKJeKAow==";

    // Small base64 encoded JPEG (just a few bytes)
    private static final String VALID_SMALL_JPEG = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAFA=";

    // Invalid base64 format (missing MIME type)
    private static final String INVALID_FORMAT = "JVBERi0xLjAKJeKAow==";

    // Invalid base64 content (not a valid base64 string)
    private static final String INVALID_CONTENT = "data:application/pdf;base64,@#$%^&*()";

    // Invalid MIME type (text file content that Tika will detect as text/plain)
    private static final String INVALID_TYPE =
            "data:application/invalid;base64,SGVsbG8gV29ybGQhIFRoaXMgaXMgYSB0ZXh0IGZpbGUu";

    private Locale defaultLocale;

    private ConstraintValidatorContext context;

    @InjectMocks
    private Base64FileListValidator base64FileListValidator;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizeInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(3);
        base64FileListValidator.initialize(base64FileValidation);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with valid base64 files, then should return true")
    @Test
    void isValid_WhenValidBase64FileList_ThenShouldReturnTrue() {
        // Arrange
        List<String> validFiles = Arrays.asList(VALID_SMALL_PDF, VALID_SMALL_JPEG);

        // Act
        boolean isValid = base64FileListValidator.isValid(validFiles, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a list with valid base64 files");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null list, then should return true")
    @Test
    void isValid_WhenNullList_ThenShouldReturnTrue() {
        // Act
        boolean isValid = base64FileListValidator.isValid(null, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a null list");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an empty list, then should return true")
    @Test
    void isValid_WhenEmptyList_ThenShouldReturnTrue() {
        // Arrange
        List<String> emptyList = new ArrayList<>();

        // Act
        boolean isValid = base64FileListValidator.isValid(emptyList, context);

        // Assert
        assertTrue(isValid, "isValid should return true for an empty list");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list exceeding max file count, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Número máximo de arquivos permitidos para envio é 3.",
            "en_US|Maximum number of allowed files is 3."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenExceedingMaxFileCount_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        List<String> tooManyFiles = Arrays.asList(
                VALID_SMALL_PDF,
                VALID_SMALL_JPEG,
                VALID_SMALL_PDF,
                VALID_SMALL_JPEG
        );

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileListValidator.isValid(tooManyFiles, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false when exceeding max file count");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with duplicate files, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|A lista não deve conter arquivos idênticos. Envie apenas arquivos únicos.",
            "en_US|The list must not contain identical files. Please send only unique files."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDuplicateFiles_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        List<String> filesWithDuplicates = Arrays.asList(VALID_SMALL_PDF, VALID_SMALL_JPEG, VALID_SMALL_PDF);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileListValidator.isValid(filesWithDuplicates, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with duplicate files");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with invalid file format, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 2º item da lista está inválido.",
            "en_US|The item #2 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidFileFormat_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        List<String> filesWithInvalid = Arrays.asList(VALID_SMALL_PDF, INVALID_FORMAT);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileListValidator.isValid(filesWithInvalid, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with an invalid file format");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with invalid file content, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 2º item da lista está inválido.",
            "en_US|The item #2 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidFileContent_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        List<String> filesWithInvalid = Arrays.asList(VALID_SMALL_PDF, INVALID_CONTENT);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileListValidator.isValid(filesWithInvalid, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with an invalid file content");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with invalid MIME type, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 2º item da lista está inválido.",
            "en_US|The item #2 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidMimeType_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        List<String> filesWithInvalid = Arrays.asList(VALID_SMALL_PDF, INVALID_TYPE);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileListValidator.isValid(filesWithInvalid, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with an invalid MIME type");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with file exceeding size limit, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 2º item da lista está inválido.",
            "en_US|The item #2 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileSizeExceedsLimit_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        byte[] largeFile = new byte[6 * 1024 * 1024]; // 6MB
        String largeBase64 = "data:application/pdf;base64," + Base64.getEncoder().encodeToString(largeFile);

        List<String> filesWithLarge = Arrays.asList(VALID_SMALL_PDF, largeBase64);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileListValidator.isValid(filesWithLarge, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with a file exceeding the size limit");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with null base64 content, then should return true")
    @Test
    void isValid_WhenNullBase64Content_ThenShouldReturnTrue() {
        // Arrange
        List<String> filesWithNull = Arrays.asList(VALID_SMALL_PDF, null);

        // Act
        boolean isValid = base64FileListValidator.isValid(filesWithNull, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a list with null base64 content");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with empty base64 content, then should return true")
    @Test
    void isValid_WhenEmptyBase64Content_ThenShouldReturnTrue() {
        // Arrange
        List<String> filesWithEmpty = Arrays.asList(VALID_SMALL_PDF, "");

        // Act
        boolean isValid = base64FileListValidator.isValid(filesWithEmpty, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a list with empty base64 content");
    }

    /**
     * Method test for
     * {@link Base64FileListValidator#initialize(Base64FileValidation)}
     */
    @Order(12)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given a negative maxFileCount, then should use default value")
    @Test
    void initialize_WhenNegativeMaxFileCount_ThenShouldUseDefaultValue() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizeInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(-1);

        Base64FileListValidator validator = new Base64FileListValidator();
        validator.initialize(base64FileValidation);

        List<String> manyFiles = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            manyFiles.add(VALID_SMALL_PDF);
        }

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = validator.isValid(manyFiles, context);

        // Assert
        assertFalse(isValid, "isValid should return false when exceeding the default max file count");
    }
}
