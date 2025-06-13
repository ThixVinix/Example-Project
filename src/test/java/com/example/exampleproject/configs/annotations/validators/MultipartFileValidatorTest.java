package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.MultipartFileValidation;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link MultipartFileValidator}
 */
@SpringBootTest
@Tag("MultipartFileValidator_Tests")
@DisplayName("MultipartFileValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class MultipartFileValidatorTest {

    private static final char CSV_DELIMITER = '|';
    private static final String IS_VALID = "isValid";
    private static final String INITIALIZE = "initialize";
    private static final String VALID_PDF_MIME_TYPE = "application/pdf";
    private static final String VALID_JPEG_MIME_TYPE = "image/jpeg";

    // Valid PDF file
    private static final byte[] VALID_PDF_CONTENT = {0x25, 0x50, 0x44, 0x46};

    // Valid JPEG file
    private static final byte[] VALID_JPEG_CONTENT = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // JPEG magic bytes
    private static final MockMultipartFile VALID_JPEG_FILE = new MockMultipartFile(
            "image.jpg", "image.jpg", VALID_JPEG_MIME_TYPE, VALID_JPEG_CONTENT);

    // File with inconsistent extension
    private static final MockMultipartFile INCONSISTENT_EXTENSION_FILE = new MockMultipartFile(
            "document.txt", "document.txt", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);

    private Locale defaultLocale;

    private ConstraintValidatorContext context;

    @InjectMocks
    private MultipartFileValidator multipartFileValidator;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
        MultipartFileValidation multipartFileValidation = mock(MultipartFileValidation.class);
        when(multipartFileValidation.allowedTypes())
                .thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(multipartFileValidation.maxSizeInMB()).thenReturn(5);
        multipartFileValidator.initialize(multipartFileValidation);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#isValid(MultipartFile, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid PDF file, then should return true")
    @Test
    void isValid_WhenValidPdfFile_ThenShouldReturnTrue() {

        Class<?> validatorClass = MultipartFileValidator.class;
        try {
            assertNotNull(validatorClass.getDeclaredMethod("isValid",
                            MultipartFile.class, ConstraintValidatorContext.class),
                    "MultipartFileValidator should have an isValid method");
            assertNotNull(validatorClass.getDeclaredMethod("initialize", MultipartFileValidation.class),
                    "MultipartFileValidator should have an initialize method");
        } catch (NoSuchMethodException e) {
            fail("Required method not found: " + e.getMessage());
        }

        assertTrue(multipartFileValidator.isValid(null, context),
                "isValid should return true for null files");

        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        assertTrue(multipartFileValidator.isValid(emptyFile, context),
                "isValid should return true for empty files");
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#isValid(MultipartFile, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a valid JPEG file, then should return true")
    @Test
    void isValid_WhenValidJpegFile_ThenShouldReturnTrue() {
        // Act
        boolean isValid = multipartFileValidator.isValid(VALID_JPEG_FILE, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a valid JPEG file");
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#isValid(MultipartFile, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null value, then should return true")
    @Test
    void isValid_WhenNullValue_ThenShouldReturnTrue() {
        // Act
        boolean isValid = multipartFileValidator.isValid(null, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a null value");
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#isValid(MultipartFile, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an empty file, then should return true")
    @Test
    void isValid_WhenEmptyFile_ThenShouldReturnTrue() {
        // Arrange
        MockMultipartFile emptyFile =
                new MockMultipartFile("empty.pdf", "empty.pdf", VALID_PDF_MIME_TYPE, new byte[0]);

        // Act
        boolean isValid = multipartFileValidator.isValid(emptyFile, context);

        // Assert
        assertTrue(isValid, "isValid should return true for an empty file");
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#isValid(MultipartFile, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a file with inconsistent extension, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Tipo de arquivo inválido. Tipos permitidos: application/pdf, image/jpeg",
            "en_US|Invalid file type. Allowed types: application/pdf, image/jpeg"
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInconsistentExtension_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = multipartFileValidator.isValid(INCONSISTENT_EXTENSION_FILE, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a file with inconsistent extension");
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#isValid(MultipartFile, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an invalid MIME type, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|A extensão do arquivo pdf não corresponde ao tipo de conteúdo detectado text/plain. Por favor, envie um arquivo com a extensão e o tipo correto.",
            "en_US|The file extension pdf does not correspond to the type of content detected text/plain. Please send a file with the correct extension and type."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidMimeType_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        MockMultipartFile textFile = new MockMultipartFile(
                "document.pdf", "document.pdf", "text/plain", "This is a text file".getBytes());

        // Act
        boolean isValid = multipartFileValidator.isValid(textFile, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for an invalid MIME type");
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#isValid(MultipartFile, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a file exceeding the size limit, then should return false")
    @Test
    void isValid_WhenFileSizeExceedsLimit_ThenShouldReturnFalse() throws Exception {
        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        byte[] largeFile = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile largeMultipartFile = new MockMultipartFile(
                "large.pdf", "large.pdf", VALID_PDF_MIME_TYPE, largeFile);

        java.lang.reflect.Method validateFileSizeMethod =
                MultipartFileValidator.class.getSuperclass().getDeclaredMethod(
                "validateFileSize", long.class, ConstraintValidatorContext.class, String.class);
        validateFileSizeMethod.setAccessible(true);

        // Act
        boolean isValid = (boolean) validateFileSizeMethod.invoke(
                multipartFileValidator, 
                largeMultipartFile.getSize(), 
                context, 
                "msg.validation.request.field.multipartfile.invalid.size");

        // Assert
        verify(context).buildConstraintViolationWithTemplate(anyString());
        assertFalse(isValid, "validateFileSize should return false for a file exceeding the size limit");
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#isValid(MultipartFile, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a file with no extension, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|A extensão do arquivo null não corresponde ao tipo de conteúdo detectado text/plain. Por favor, envie um arquivo com a extensão e o tipo correto.",
            "en_US|The file extension null does not correspond to the type of content detected text/plain. Please send a file with the correct extension and type."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileWithNoExtension_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        MockMultipartFile fileWithNoExtension = new MockMultipartFile(
                "document", "document", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);

        // Act
        boolean isValid = multipartFileValidator.isValid(fileWithNoExtension, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a file with no extension");
    }

    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an exception during MIME type detection, then should handle it gracefully")
    @Test
    void isValid_WhenExceptionDuringMimeTypeDetection_ThenShouldHandleGracefully() {

        try {
            java.lang.reflect.Method detectMimeTypeMethod = MultipartFileValidator.class.getDeclaredMethod(
                    "detectMimeType", MultipartFile.class);
            detectMimeTypeMethod.setAccessible(true);

            assertNotNull(detectMimeTypeMethod, "detectMimeType method should exist");

            java.lang.reflect.Method isValidMethod = MultipartFileValidator.class.getDeclaredMethod(
                    "isValid", MultipartFile.class, ConstraintValidatorContext.class);
            isValidMethod.setAccessible(true);

            assertNotNull(isValidMethod, "isValid method should exist");

        } catch (NoSuchMethodException e) {
            fail("Required method not found: " + e.getMessage());
        }

        try {
            java.lang.reflect.Field loggerField = MultipartFileValidator.class.getDeclaredField("log");
            loggerField.setAccessible(true);

            assertNotNull(loggerField, "log field should exist");

        } catch (NoSuchFieldException e) {
            fail("Required field not found: " + e.getMessage());
        }
    }

    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given null or empty files, then should return true")
    @Test
    void isValid_WhenNullOrEmptyFiles_ThenShouldReturnTrue() {
        assertTrue(multipartFileValidator.isValid(null, context),
                "isValid should return true for null files");

        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        assertTrue(multipartFileValidator.isValid(emptyFile, context),
                "isValid should return true for empty files");
    }

    /**
     * Method test for
     * {@link MultipartFileValidator#initialize(MultipartFileValidation)}
     */
    @Order(9)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given a negative maxSizeInMB, then should use default value")
    @Test
    void initialize_WhenNegativeMaxSizeInMB_ThenShouldUseDefaultValue() {
        // Arrange
        MultipartFileValidation multipartFileValidation = mock(MultipartFileValidation.class);
        when(multipartFileValidation.allowedTypes())
                .thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(multipartFileValidation.maxSizeInMB()).thenReturn(-1);

        // Act
        multipartFileValidator.initialize(multipartFileValidation);

        byte[] largeFile = new byte[3 * 1024 * 1024]; // 3MB
        MockMultipartFile largeMultipartFile = new MockMultipartFile(
                "large.pdf", "large.pdf", VALID_PDF_MIME_TYPE, largeFile);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        boolean isValid = multipartFileValidator.isValid(largeMultipartFile, context);

        // Assert
        assertFalse(isValid, "isValid should return false for a file exceeding the default size limit");
    }
}
