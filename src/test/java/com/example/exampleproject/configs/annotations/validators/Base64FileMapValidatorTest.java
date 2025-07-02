package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.MessageConfig;
import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.utils.MessageUtils;
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

import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link Base64FileMapValidator}
 */
@SpringBootTest(classes = {MessageConfig.class, MessageUtils.class})
@Tag("Base64FileMapValidator_Tests")
@DisplayName("Base64FileMapValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class Base64FileMapValidatorTest {

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

    // Valid file names
    private static final String VALID_PDF_NAME = "document.pdf";
    private static final String VALID_JPEG_NAME = "image.jpg";
    private static final String INVALID_NAME = "invalid@file.pdf";

    private Locale defaultLocale;

    private ConstraintValidatorContext context;

    @InjectMocks
    private Base64FileMapValidator base64FileMapValidator;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(3);
        base64FileMapValidator.initialize(base64FileValidation);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with valid base64 files, then should return true")
    @Test
    void isValid_WhenValidBase64FileMap_ThenShouldReturnTrue() {
        // Arrange
        Map<String, String> validFiles = new HashMap<>();
        validFiles.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        validFiles.put(VALID_JPEG_NAME, VALID_SMALL_JPEG);

        // Act
        boolean isValid = base64FileMapValidator.isValid(validFiles, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a map with valid base64 files");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null map, then should return true")
    @Test
    void isValid_WhenNullMap_ThenShouldReturnTrue() {
        // Act
        boolean isValid = base64FileMapValidator.isValid(null, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a null map");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an empty map, then should return true")
    @Test
    void isValid_WhenEmptyMap_ThenShouldReturnTrue() {
        // Arrange
        Map<String, String> emptyMap = new HashMap<>();

        // Act
        boolean isValid = base64FileMapValidator.isValid(emptyMap, context);

        // Assert
        assertTrue(isValid, "isValid should return true for an empty map");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map exceeding max file count, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Número máximo de arquivos permitidos para envio é 3.",
            "en_US|Maximum number of allowed files is 3."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenExceedingMaxFileCount_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Map<String, String> tooManyFiles = new HashMap<>();
        tooManyFiles.put("file1.pdf", VALID_SMALL_PDF);
        tooManyFiles.put("file2.jpg", VALID_SMALL_JPEG);
        tooManyFiles.put("file3.pdf", VALID_SMALL_PDF);
        tooManyFiles.put("file4.jpg", VALID_SMALL_JPEG);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = base64FileMapValidator.isValid(tooManyFiles, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false when exceeding max file count");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with invalid file name, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O nome do arquivo invalid@file.pdf, do 2º item da lista, é inválido. O nome do arquivo deve incluir uma extensão (ex: arquivo.pdf) e conter apenas caracteres válidos: letras (a-z, A-Z), números (0-9), underscores (_), hífens (-) e exatamente um ponto (.) para separar o nome e a extensão.",
            "en_US|The file name invalid@file.pdf, from the item #2 in the list, is invalid. The filename must include an extension (e.g., file.pdf) and contain only valid characters: letters (a-z, A-Z), numbers (0-9), underscores (_), hyphens (-), and exactly one dot (.) to separate the name and extension."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidFileName_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithInvalidName = new HashMap<>();
        filesWithInvalidName.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        filesWithInvalidName.put(INVALID_NAME, VALID_SMALL_JPEG);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithInvalidName, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with an invalid file name");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with invalid file format, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 2º item da lista está inválido.",
            "en_US|The item #2 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidFileFormat_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithInvalidFormat = new HashMap<>();
        filesWithInvalidFormat.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        filesWithInvalidFormat.put("invalid.pdf", INVALID_FORMAT);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithInvalidFormat, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with an invalid file format");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with invalid file content, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 2º item da lista está inválido.",
            "en_US|The item #2 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidFileContent_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithInvalidContent = new HashMap<>();
        filesWithInvalidContent.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        filesWithInvalidContent.put("invalid.pdf", INVALID_CONTENT);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithInvalidContent, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with an invalid file content");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with invalid MIME type, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 2º item da lista está inválido.",
            "en_US|The item #2 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidMimeType_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithInvalidType = new HashMap<>();
        filesWithInvalidType.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        filesWithInvalidType.put("invalid.pdf", INVALID_TYPE);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithInvalidType, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with an invalid MIME type");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with file exceeding size limit, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 2º item da lista está inválido.",
            "en_US|The item #2 in the list is invalid."
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

        Map<String, String> filesWithLarge = new HashMap<>();
        filesWithLarge.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        filesWithLarge.put("large.pdf", largeBase64);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithLarge, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with a file exceeding the size limit");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with duplicate file content, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|A lista não deve conter arquivos idênticos. Envie apenas arquivos únicos.",
            "en_US|The list must not contain identical files. Please send only unique files."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDuplicateFileContent_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithDuplicateContent = new HashMap<>();
        filesWithDuplicateContent.put("document1.pdf", VALID_SMALL_PDF);
        filesWithDuplicateContent.put("document2.pdf", VALID_SMALL_PDF); // Same content as document1.pdf

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithDuplicateContent, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with duplicate file content");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with null file name, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O nome do arquivo, do 1º item da lista, não foi fornecido.",
            "en_US|The file name, from the item #1 in the list, was not provided."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenNullFileName_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithNullKey = new HashMap<>();
        filesWithNullKey.put(null, VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithNullKey, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with a null file name");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(12)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with empty file name, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O nome do arquivo, do 1º item da lista, não foi fornecido.",
            "en_US|The file name, from the item #1 in the list, was not provided."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenEmptyFileName_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithEmptyKey = new HashMap<>();
        filesWithEmptyKey.put("", VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithEmptyKey, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with an empty file name");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(13)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with null base64 content, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O conteúdo Base64 do arquivo document.pdf, do 1º item da lista, não foi fornecido.",
            "en_US|The Base64 content of the file document.pdf, from the item #1 in the list, was not provided."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenNullBase64Content_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithNullContent = new HashMap<>();
        filesWithNullContent.put(VALID_PDF_NAME, null);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithNullContent, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with null base64 content");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(14)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with empty base64 content, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O conteúdo Base64 do arquivo document.pdf, do 1º item da lista, não foi fornecido.",
            "en_US|The Base64 content of the file document.pdf, from the item #1 in the list, was not provided."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenEmptyBase64Content_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithEmptyContent = new HashMap<>();
        filesWithEmptyContent.put(VALID_PDF_NAME, "");

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithEmptyContent, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with empty base64 content");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(15)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with file extension not matching MIME type, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O arquivo document.jpg, do 1º item da lista, possui extensão jpg que não corresponde à extensão esperada pdf com base no conteúdo do arquivo.",
            "en_US|The file document.jpg, from the item #1 in the list, has extension jpg which does not match the expected extension pdf based on the file content."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileExtensionNotMatchingMimeType_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithMismatchedExtension = new HashMap<>();

        filesWithMismatchedExtension.put("document.jpg", VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithMismatchedExtension, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with file extension not matching MIME type");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(16)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with unsupported MIME type, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 1º item da lista está inválido.",
            "en_US|The item #1 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenUnsupportedMimeType_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        String unsupportedMimeType = "data:application/unsupported;base64,SGVsbG8gV29ybGQh";

        Map<String, String> filesWithUnsupportedMimeType = new HashMap<>();
        filesWithUnsupportedMimeType.put("document.bin", unsupportedMimeType);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithUnsupportedMimeType, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with unsupported MIME type");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(17)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with file having invalid extension, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O arquivo document.xyz, do 1º item da lista, possui uma extensão inválida xyz. A extensão deve ser uma das extensões de arquivo suportadas pelo sistema.",
            "en_US|The file document.xyz, from the item #1 in the list, has an invalid extension xyz. The extension must be one of the supported file types."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidFileExtension_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithInvalidExtension = new HashMap<>();

        filesWithInvalidExtension.put("document.xyz", VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithInvalidExtension, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with a file having invalid extension");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(18)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with file having no extension, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O nome do arquivo document, do 1º item da lista, é inválido. O nome do arquivo deve incluir uma extensão (ex: arquivo.pdf) e conter apenas caracteres válidos: letras (a-z, A-Z), números (0-9), underscores (_), hífens (-) e exatamente um ponto (.) para separar o nome e a extensão.",
            "en_US|The file name document, from the item #1 in the list, is invalid. The filename must include an extension (e.g., file.pdf) and contain only valid characters: letters (a-z, A-Z), numbers (0-9), underscores (_), hyphens (-), and exactly one dot (.) to separate the name and extension."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileWithNoExtension_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithNoExtension = new HashMap<>();

        filesWithNoExtension.put("document", VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithNoExtension, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with a file having no extension");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(19)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with malformed base64 string, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 1º item da lista está inválido.",
            "en_US|The item #1 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenMalformedBase64String_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithMalformedBase64 = new HashMap<>();

        filesWithMalformedBase64.put(VALID_PDF_NAME, "data:base64,JVBERi0xLjAKJeKAow==");

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithMalformedBase64, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with a malformed base64 string");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(20)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null expected extension, then should return false with unsupported filetype message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O tipo de arquivo enviado, do 1º item da lista, não é suportado pelo sistema.",
            "en_US|The file type sent, from the item #1 in the list, is not supported by the system."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenNullExpectedExtension_ThenShouldReturnFalse(String languageTag, String expectedMessage) throws Exception {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        java.lang.reflect.Method validateMimeTypeSupported = Base64FileMapValidator.class.getDeclaredMethod(
                "validateMimeTypeSupported", String.class, int.class, ConstraintValidatorContext.class);
        validateMimeTypeSupported.setAccessible(true);

        boolean result = (boolean) validateMimeTypeSupported.invoke(base64FileMapValidator, null, 0, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(result, "validateMimeTypeSupported should return false when expectedExtension is null");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * This test specifically targets the condition in validateMimeTypeSupported where expectedExtension is null
     */
    @Order(21)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with unknown MIME type, then should return false with unsupported filetype message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O tipo de arquivo enviado, do 1º item da lista, não é suportado pelo sistema.",
            "en_US|The file type sent, from the item #1 in the list, is not supported by the system."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenUnknownMimeType_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        String customMimeType = "data:application/pdf-custom;base64,JVBERi0xLjAKJeKAow==";

        Map<String, String> filesWithCustomMimeType = new HashMap<>();
        filesWithCustomMimeType.put("document.custom", customMimeType);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithCustomMimeType, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a map with a custom MIME type not in MimeTypeEnum");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#initialize(Base64FileValidation)}
     */
    @Order(22)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given a negative maxFileCount, then should use default value")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|Número máximo de arquivos permitidos para envio é -1.",
            "en_US|Maximum number of allowed files is -1."
    }, delimiter = CSV_DELIMITER)
    void initialize_WhenNegativeMaxFileCount_ThenShouldUseDefaultValue(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(-1);

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        Map<String, String> manyFiles = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            manyFiles.put("file" + i + ".pdf", VALID_SMALL_PDF);
        }

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = validator.isValid(manyFiles, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false when exceeding the default max file count");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(23)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a file name with multiple dots, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O nome do arquivo file.name.pdf, do 1º item da lista, é inválido. O nome do arquivo deve incluir uma extensão (ex: arquivo.pdf) e conter apenas caracteres válidos: letras (a-z, A-Z), números (0-9), underscores (_), hífens (-) e exatamente um ponto (.) para separar o nome e a extensão.",
            "en_US|The file name file.name.pdf, from the item #1 in the list, is invalid. The filename must include an extension (e.g., file.pdf) and contain only valid characters: letters (a-z, A-Z), numbers (0-9), underscores (_), hyphens (-), and exactly one dot (.) to separate the name and extension."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileNameWithMultipleDots_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithInvalidName = new HashMap<>();
        filesWithInvalidName.put("file.name.pdf", VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithInvalidName, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a file name with multiple dots");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(24)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a file name starting with dot, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O nome do arquivo .hidden.pdf, do 1º item da lista, é inválido. O nome do arquivo deve incluir uma extensão (ex: arquivo.pdf) e conter apenas caracteres válidos: letras (a-z, A-Z), números (0-9), underscores (_), hífens (-) e exatamente um ponto (.) para separar o nome e a extensão.",
            "en_US|The file name .hidden.pdf, from the item #1 in the list, is invalid. The filename must include an extension (e.g., file.pdf) and contain only valid characters: letters (a-z, A-Z), numbers (0-9), underscores (_), hyphens (-), and exactly one dot (.) to separate the name and extension."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileNameStartingWithDot_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithInvalidName = new HashMap<>();
        filesWithInvalidName.put(".hidden.pdf", VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithInvalidName, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a file name starting with dot");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(25)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a file name with spaces, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O nome do arquivo file name.pdf, do 1º item da lista, é inválido. O nome do arquivo deve incluir uma extensão (ex: arquivo.pdf) e conter apenas caracteres válidos: letras (a-z, A-Z), números (0-9), underscores (_), hífens (-) e exatamente um ponto (.) para separar o nome e a extensão.",
            "en_US|The file name file name.pdf, from the item #1 in the list, is invalid. The filename must include an extension (e.g., file.pdf) and contain only valid characters: letters (a-z, A-Z), numbers (0-9), underscores (_), hyphens (-), and exactly one dot (.) to separate the name and extension."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileNameWithSpaces_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithInvalidName = new HashMap<>();
        filesWithInvalidName.put("file name.pdf", VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithInvalidName, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a file name with spaces");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(26)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given zero maxTotalSizeInMB, then should skip total size validation")
    @Test
    void isValid_WhenZeroMaxTotalSizeInMB_ThenShouldSkipTotalSizeValidation() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(3);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(0);

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        Map<String, String> validFiles = new HashMap<>();
        validFiles.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        validFiles.put(VALID_JPEG_NAME, VALID_SMALL_JPEG);

        // Act
        boolean isValid = validator.isValid(validFiles, context);

        // Assert
        assertTrue(isValid, "isValid should return true when maxTotalSizeInMB is zero (disabled)");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(27)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with whitespace-only file name, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O nome do arquivo, do 1º item da lista, não foi fornecido.",
            "en_US|The file name, from the item #1 in the list, was not provided."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenWhitespaceOnlyFileName_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithWhitespaceFileName = new HashMap<>();
        filesWithWhitespaceFileName.put("   ", VALID_SMALL_PDF);

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithWhitespaceFileName, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a whitespace-only file name");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(28)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with blank base64 content, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O conteúdo Base64 do arquivo document.pdf, do 1º item da lista, não foi fornecido.",
            "en_US|The Base64 content of the file document.pdf, from the item #1 in the list, was not provided."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenBlankBase64Content_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithBlankContent = new HashMap<>();
        filesWithBlankContent.put(VALID_PDF_NAME, "   ");

        // Act
        boolean isValid = base64FileMapValidator.isValid(filesWithBlankContent, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for blank base64 content");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * Testing validateTotalSize method conditions
     */
    @Order(29)
    @Tag(value = IS_VALID)
    @DisplayName("validateTotalSize - Given negative maxTotalSizeInMB, then should skip total size validation")
    @Test
    void validateTotalSize_WhenNegativeMaxTotalSizeInMB_ThenShouldSkipTotalSizeValidation() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(3);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(-1);

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        Map<String, String> validFiles = new HashMap<>();
        validFiles.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        validFiles.put(VALID_JPEG_NAME, VALID_SMALL_JPEG);

        // Act
        boolean isValid = validator.isValid(validFiles, context);

        // Assert
        assertTrue(isValid, "isValid should return true when maxTotalSizeMB is negative (disabled)");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * Testing validateTotalSize method conditions
     */
    @Order(30)
    @Tag(value = IS_VALID)
    @DisplayName("validateTotalSize - Given total size within limit, then should return true")
    @Test
    void validateTotalSize_WhenTotalSizeWithinLimit_ThenShouldReturnTrue() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(3);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(10); // 10MB limit

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        Map<String, String> validFiles = new HashMap<>();
        validFiles.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        validFiles.put(VALID_JPEG_NAME, VALID_SMALL_JPEG);

        // Act
        boolean isValid = validator.isValid(validFiles, context);

        // Assert
        assertTrue(isValid, "isValid should return true when total size is within the limit");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * Testing validateTotalSize method conditions
     */
    @Order(31)
    @Tag(value = IS_VALID)
    @DisplayName("validateTotalSize - Given total size exceeding limit, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O tamanho total de todos os arquivos é de 8,0000 MB, excede o limite permitido de 5 MB.",
            "en_US|The total size of all files is 8,0000 MB, exceeding the allowed limit of 5 MB."
    }, delimiter = CSV_DELIMITER)
    void validateTotalSize_WhenTotalSizeExceedingLimit_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(10);
        when(base64FileValidation.maxFileCount()).thenReturn(5);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(5); // 5MB limit

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        // Create files that exceed the total size limit (2 files of 4MB each = 8MB > 5MB limit)
        byte[] largeFile = new byte[4 * 1024 * 1024]; // 4MB each
        String largeBase64 = "data:application/pdf;base64," + Base64.getEncoder().encodeToString(largeFile);

        Map<String, String> filesExceedingTotalSize = new HashMap<>();
        filesExceedingTotalSize.put("file1.pdf", largeBase64);
        filesExceedingTotalSize.put("file2.pdf", largeBase64);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = validator.isValid(filesExceedingTotalSize, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false when total size exceeds limit");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * Testing validateTotalSize method conditions
     */
    @Order(32)
    @Tag(value = IS_VALID)
    @DisplayName("validateTotalSize - Given map with null values, then should skip null values in calculation")
    @Test
    void validateTotalSize_WhenMapWithNullValues_ThenShouldSkipNullValuesInCalculation() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(5);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(10); // 10MB limit

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithNullValues = new HashMap<>();
        filesWithNullValues.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        filesWithNullValues.put("null-file.pdf", null);
        filesWithNullValues.put(VALID_JPEG_NAME, VALID_SMALL_JPEG);

        // Act
        boolean isValid = validator.isValid(filesWithNullValues, context);

        // Assert
        assertFalse(isValid, "isValid should return false when map contains null values (validation should fail for null content)");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * Testing validateTotalSize method conditions
     */
    @Order(33)
    @Tag(value = IS_VALID)
    @DisplayName("validateTotalSize - Given map with only null values, then should return false")
    @Test
    void validateTotalSize_WhenMapWithOnlyNullValues_ThenShouldReturnFalse() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(5);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(1); // Very small limit

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithOnlyNullValues = new HashMap<>();
        filesWithOnlyNullValues.put("file1.pdf", null);
        filesWithOnlyNullValues.put("file2.pdf", null);

        // Act
        boolean isValid = validator.isValid(filesWithOnlyNullValues, context);

        // Assert
        assertFalse(isValid, "isValid should return false when map contains only null values (validation should fail for null content)");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * Testing validateTotalSize method conditions
     */
    @Order(34)
    @Tag(value = IS_VALID)
    @DisplayName("validateTotalSize - Given empty map, then should return true")
    @Test
    void validateTotalSize_WhenEmptyMap_ThenShouldReturnTrue() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(5);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(1); // Very small limit

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        Map<String, String> emptyMap = new HashMap<>();

        // Act
        boolean isValid = validator.isValid(emptyMap, context);

        // Assert
        assertTrue(isValid, "isValid should return true for empty map");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * Testing validateTotalSize method conditions
     */
    @Order(35)
    @Tag(value = IS_VALID)
    @DisplayName("validateTotalSize - Given map with empty base64 content, then should return false")
    @Test
    void validateTotalSize_WhenMapWithEmptyBase64Content_ThenShouldReturnFalse() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(5);
        when(base64FileValidation.maxFileCount()).thenReturn(5);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(10); // 10MB limit

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        Map<String, String> filesWithEmptyContent = new HashMap<>();
        filesWithEmptyContent.put(VALID_PDF_NAME, VALID_SMALL_PDF);
        filesWithEmptyContent.put("empty-file.pdf", "");
        filesWithEmptyContent.put(VALID_JPEG_NAME, VALID_SMALL_JPEG);

        // Act
        boolean isValid = validator.isValid(filesWithEmptyContent, context);

        // Assert
        assertFalse(isValid, "isValid should return false when map contains empty base64 content " +
                "(validation should fail for empty content)");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     * Testing validateTotalSize method conditions
     */
    @Order(36)
    @Tag(value = IS_VALID)
    @DisplayName("validateTotalSize - Given exactly at size limit, then should return false due to MIME type detection")
    @Test
    void validateTotalSize_WhenExactlyAtSizeLimit_ThenShouldReturnFalse() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizePerFileInMB()).thenReturn(10);
        when(base64FileValidation.maxFileCount()).thenReturn(5);
        when(base64FileValidation.maxTotalSizeInMB()).thenReturn(4); // 4MB limit

        Base64FileMapValidator validator = new Base64FileMapValidator();
        validator.initialize(base64FileValidation);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Create a file that is exactly 4MB (but will be detected as octet-stream by Tika)
        byte[] exactSizeFile = new byte[4 * 1024 * 1024]; // Exactly 4MB
        String exactSizeBase64 = "data:application/pdf;base64," + Base64.getEncoder().encodeToString(exactSizeFile);

        Map<String, String> filesAtExactLimit = new HashMap<>();
        filesAtExactLimit.put("exact-size.pdf", exactSizeBase64);

        // Act
        boolean isValid = validator.isValid(filesAtExactLimit, context);

        // Assert
        assertFalse(isValid, 
                "isValid should return false because Tika detects the file as octet-stream, not PDF");
    }
}
