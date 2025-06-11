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

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link Base64FileValidator}
 */
@SpringBootTest
@Tag("Base64FileValidator_Tests")
@DisplayName("Base64FileValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class Base64FileValidatorTest {

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
    private Base64FileValidator base64FileValidator;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizeInMB()).thenReturn(5);
        base64FileValidator.initialize(base64FileValidation);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid PDF base64 string, then should return true")
    @Test
    void isValid_WhenValidPdfBase64_ThenShouldReturnTrue() {
        // Act
        boolean isValid = base64FileValidator.isValid(VALID_SMALL_PDF, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid PDF base64 string");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid JPEG base64 string, then should return true")
    @Test
    void isValid_WhenValidJpegBase64_ThenShouldReturnTrue() {
        // Act
        boolean isValid = base64FileValidator.isValid(VALID_SMALL_JPEG, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid JPEG base64 string");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null value, then should return true")
    @Test
    void isValid_WhenNullValue_ThenShouldReturnTrue() {
        // Act
        boolean isValid = base64FileValidator.isValid(null, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a null value");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an empty string, then should return true")
    @Test
    void isValid_WhenEmptyString_ThenShouldReturnTrue() {
        // Act
        boolean isValid = base64FileValidator.isValid("", context);

        // Assert
        assertTrue(isValid, "isValid should return true for an empty string");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid format, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Formato de arquivo base64 inválido. Formato esperado: data:[tipo]/[subtipo];base64,[conteúdo]",
            "en_US|Invalid base64 file format. Expected format: data:[type]/[subtype];base64,[content]"
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidFormat_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileValidator.isValid(INVALID_FORMAT, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for an invalid format");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid MIME type, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O tipo de arquivo detectado text/plain não é permitido. Tipos esperados: application/pdf, image/jpeg.",
            "en_US|The file type detected text/plain is not allowed. Expected types: application/pdf, image/jpeg."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidMimeType_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileValidator.isValid(INVALID_TYPE, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for an invalid MIME type");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given invalid base64 content, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O conteúdo não é um arquivo codificado em base64 válido.",
            "en_US|The content is not a valid base64-encoded file."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidBase64Content_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileValidator.isValid(INVALID_CONTENT, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for invalid base64 content");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a file exceeding the size limit, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O tamanho do arquivo enviado é de 6,0000 MB, excede o limite permitido de 5 MB.",
            "en_US|The file size is 6,0000 MB, exceeding the allowed limit of 5 MB."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileSizeExceedsLimit_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        byte[] largeFile = new byte[6 * 1024 * 1024]; // 6MB
        String largeBase64 = "data:application/pdf;base64," + Base64.getEncoder().encodeToString(largeFile);

        // Act
        boolean isValid = base64FileValidator.isValid(largeBase64, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a file exceeding the size limit");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#isValid(String, ConstraintValidatorContext)}
     */
    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an exception during MIME type detection, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Ocorreu um erro inesperado ao validar o arquivo. Por favor, tente novamente ou utilize um arquivo diferente.",
            "en_US|An unexpected error occurred by validating the file. Please try again or use a different file."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenExceptionDuringMimeTypeDetection_ThenShouldReturnFalse(String languageTag,
                                                                            String expectedMessage) throws Exception {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Set Tika field to null to cause a NullPointerException
        Field tikaField = Base64FileValidator.class.getDeclaredField("tika");
        tikaField.setAccessible(true);
        tikaField.set(base64FileValidator, null);

        // Act
        boolean isValid = base64FileValidator.isValid(VALID_SMALL_PDF, context);

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false when an exception occurs during MIME type detection");
    }

    /**
     * Method test for
     * {@link Base64FileValidator#initialize(Base64FileValidation)}
     */
    @Order(10)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given a negative maxSizeInMB, then should use default value")
    @Test
    void initialize_WhenNegativeMaxSizeInMB_ThenShouldUseDefaultValue() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizeInMB()).thenReturn(-1);

        // Act
        base64FileValidator.initialize(base64FileValidation);

        byte[] largeFile = new byte[6 * 1024 * 1024]; // 6MB
        String largeBase64 = "data:application/pdf;base64," + Base64.getEncoder().encodeToString(largeFile);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        boolean isValid = base64FileValidator.isValid(largeBase64, context);

        assertFalse(isValid, "isValid should return false for a file exceeding the default size limit");
    }
}
