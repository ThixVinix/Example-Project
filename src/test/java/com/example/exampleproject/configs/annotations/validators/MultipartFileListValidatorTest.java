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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link MultipartFileListValidator}
 */
@SpringBootTest
@Tag("MultipartFileListValidator_Tests")
@DisplayName("MultipartFileListValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class MultipartFileListValidatorTest {

    private static final char CSV_DELIMITER = '|';
    private static final String IS_VALID = "isValid";
    private static final String INITIALIZE = "initialize";
    private static final String VALID_PDF_MIME_TYPE = "application/pdf";
    private static final String VALID_JPEG_MIME_TYPE = "image/jpeg";

    // Valid PDF file
    private static final byte[] VALID_PDF_CONTENT = {0x25, 0x50, 0x44, 0x46}; // %PDF magic bytes
    private static final MockMultipartFile VALID_PDF_FILE = new MockMultipartFile(
            "document.pdf", "document.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);

    // Valid JPEG file
    private static final byte[] VALID_JPEG_CONTENT = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // JPEG magic bytes
    private static final MockMultipartFile VALID_JPEG_FILE = new MockMultipartFile(
            "image.jpg", "image.jpg", VALID_JPEG_MIME_TYPE, VALID_JPEG_CONTENT);

    // Invalid file type (text file with PDF extension)
    private static final byte[] INVALID_TYPE_CONTENT = "This is a text file".getBytes();
    private static final MockMultipartFile INVALID_TYPE_FILE = new MockMultipartFile(
            "document.pdf", "document.pdf", "text/plain", INVALID_TYPE_CONTENT);

    private Locale defaultLocale;

    private ConstraintValidatorContext context;

    @InjectMocks
    private MultipartFileListValidator multipartFileListValidator;

    @BeforeEach
    void setUp() {
        defaultLocale = LocaleContextHolder.getLocale();
        context = mock(ConstraintValidatorContext.class);
        MultipartFileValidation multipartFileValidation = mock(MultipartFileValidation.class);
        when(multipartFileValidation.allowedTypes())
                .thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(multipartFileValidation.maxSizeInMB()).thenReturn(5);
        when(multipartFileValidation.maxFileCount()).thenReturn(3);
        multipartFileListValidator.initialize(multipartFileValidation);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.setLocale(defaultLocale);
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(1)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with valid files, then should return true")
    @Test
    void isValid_WhenValidFileList_ThenShouldReturnTrue() {
        // Arrange
        MultipartFileListValidator validator = new MultipartFileListValidator();
        MultipartFileValidation annotation = mock(MultipartFileValidation.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(annotation.maxSizeInMB()).thenReturn(5);
        when(annotation.maxFileCount()).thenReturn(3);

        MultipartFileValidator mockFileValidator = mock(MultipartFileValidator.class);
        when(mockFileValidator
                .isValid(any(MultipartFile.class), any(ConstraintValidatorContext.class))).thenReturn(true);

        try {
            java.lang.reflect.Field field =
                    MultipartFileListValidator.class.getDeclaredField("multipartFileValidator");
            field.setAccessible(true);

            validator.initialize(annotation);
            field.set(validator, mockFileValidator);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        List<MultipartFile> validFiles = Arrays.asList(VALID_PDF_FILE, VALID_JPEG_FILE);

        // Act
        boolean isValid = validator.isValid(validFiles, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a list with valid files");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(2)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a null list, then should return true")
    @Test
    void isValid_WhenNullList_ThenShouldReturnTrue() {
        // Act
        boolean isValid = multipartFileListValidator.isValid(null, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a null list");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(3)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given an empty list, then should return true")
    @Test
    void isValid_WhenEmptyList_ThenShouldReturnTrue() {
        // Arrange
        List<MultipartFile> emptyList = new ArrayList<>();

        // Act
        boolean isValid = multipartFileListValidator.isValid(emptyList, context);

        // Assert
        assertTrue(isValid, "isValid should return true for an empty list");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(4)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with null file, then should skip it and return true")
    @Test
    void isValid_WhenListWithNullFile_ThenShouldSkipItAndReturnTrue() {
        // Arrange
        MultipartFileListValidator validator = new MultipartFileListValidator();
        MultipartFileValidation annotation = mock(MultipartFileValidation.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(annotation.maxSizeInMB()).thenReturn(5);
        when(annotation.maxFileCount()).thenReturn(3);

        MultipartFileValidator mockFileValidator = mock(MultipartFileValidator.class);
        when(mockFileValidator
                .isValid(any(MultipartFile.class), any(ConstraintValidatorContext.class))).thenReturn(true);

        try {
            java.lang.reflect.Field field =
                    MultipartFileListValidator.class.getDeclaredField("multipartFileValidator");
            field.setAccessible(true);

            validator.initialize(annotation);
            field.set(validator, mockFileValidator);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        List<MultipartFile> filesWithNull = Arrays.asList(VALID_PDF_FILE, null, VALID_JPEG_FILE);

        // Act
        boolean isValid = validator.isValid(filesWithNull, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a list with a null file");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(5)
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
        List<MultipartFile> tooManyFiles = Arrays.asList(
                VALID_PDF_FILE, 
                VALID_JPEG_FILE,
                new MockMultipartFile(
                        "doc3.pdf", "doc3.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT),
                new MockMultipartFile(
                        "doc4.pdf", "doc4.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT)
        );

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = multipartFileListValidator.isValid(tooManyFiles, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false when exceeding max file count");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with duplicate files, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 1º item da lista está inválido.",
            "en_US|The item #1 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDuplicateFiles_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MockMultipartFile duplicateFile1 = new MockMultipartFile(
                "duplicate.pdf", "duplicate.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);
        MockMultipartFile duplicateFile2 = new MockMultipartFile(
                "duplicate.pdf", "duplicate.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);

        List<MultipartFile> filesWithDuplicates = Arrays.asList(VALID_PDF_FILE, duplicateFile1, duplicateFile2);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = multipartFileListValidator.isValid(filesWithDuplicates, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with duplicate files");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with invalid file, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 1º item da lista está inválido.",
            "en_US|The item #1 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenInvalidFile_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        List<MultipartFile> filesWithInvalid = Arrays.asList(VALID_PDF_FILE, INVALID_TYPE_FILE);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = multipartFileListValidator.isValid(filesWithInvalid, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with an invalid file");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#initialize(MultipartFileValidation)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with file having null filename, then should skip filename check")
    @Test
    void isValid_WhenFileWithNullFilename_ThenShouldSkipFilenameCheck() {
        // Arrange
        MultipartFileListValidator validator = new MultipartFileListValidator();
        MultipartFileValidation annotation = mock(MultipartFileValidation.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(annotation.maxSizeInMB()).thenReturn(5);
        when(annotation.maxFileCount()).thenReturn(3);

        MultipartFileValidator mockFileValidator = mock(MultipartFileValidator.class);
        when(mockFileValidator
                .isValid(any(MultipartFile.class), any(ConstraintValidatorContext.class))).thenReturn(true);

        try {
            java.lang.reflect.Field field =
                    MultipartFileListValidator.class.getDeclaredField("multipartFileValidator");
            field.setAccessible(true);

            validator.initialize(annotation);
            field.set(validator, mockFileValidator);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        MultipartFile fileWithNullFilename = mock(MultipartFile.class);
        when(fileWithNullFilename.getOriginalFilename()).thenReturn(null);

        List<MultipartFile> filesWithNullFilename = Arrays.asList(VALID_PDF_FILE, fileWithNullFilename);

        // Act
        boolean isValid = validator.isValid(filesWithNullFilename, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a list with a file having null filename");
    }

    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with file having empty filename, then should skip filename check")
    @Test
    void isValid_WhenFileWithEmptyFilename_ThenShouldSkipFilenameCheck() {
        // Arrange
        MultipartFileListValidator validator = new MultipartFileListValidator();
        MultipartFileValidation annotation = mock(MultipartFileValidation.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(annotation.maxSizeInMB()).thenReturn(5);
        when(annotation.maxFileCount()).thenReturn(3);

        MultipartFileValidator mockFileValidator = mock(MultipartFileValidator.class);
        when(mockFileValidator
                .isValid(any(MultipartFile.class), any(ConstraintValidatorContext.class))).thenReturn(true);

        try {
            java.lang.reflect.Field field =
                    MultipartFileListValidator.class.getDeclaredField("multipartFileValidator");
            field.setAccessible(true);

            validator.initialize(annotation);
            field.set(validator, mockFileValidator);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        MultipartFile fileWithEmptyFilename = mock(MultipartFile.class);
        when(fileWithEmptyFilename.getOriginalFilename()).thenReturn("");

        List<MultipartFile> filesWithEmptyFilename = Arrays.asList(VALID_PDF_FILE, fileWithEmptyFilename);

        // Act
        boolean isValid = validator.isValid(filesWithEmptyFilename, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a list with a file having empty filename");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(10)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with mix of valid and invalid files, then should return false")
    @Test
    void isValid_WhenMixOfValidAndInvalidFiles_ThenShouldReturnFalse() {
        // Arrange
        List<MultipartFile> mixedFiles = Arrays.asList(VALID_PDF_FILE, INVALID_TYPE_FILE);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = multipartFileListValidator.isValid(mixedFiles, context);

        // Assert
        assertFalse(isValid, "isValid should return false for a list with a mix of valid and invalid files");
        verify(context, atLeastOnce()).disableDefaultConstraintViolation();
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(anyString());
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(11)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with files having same content but different filenames, then should return true")
    @Test
    void isValid_WhenFilesWithSameContentButDifferentFilenames_ThenShouldReturnTrue() {
        // Arrange
        MultipartFileListValidator validator = new MultipartFileListValidator();
        MultipartFileValidation annotation = mock(MultipartFileValidation.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(annotation.maxSizeInMB()).thenReturn(5);
        when(annotation.maxFileCount()).thenReturn(3);

        MultipartFileValidator mockFileValidator = mock(MultipartFileValidator.class);
        when(mockFileValidator
                .isValid(any(MultipartFile.class), any(ConstraintValidatorContext.class))).thenReturn(true);

        try {
            java.lang.reflect.Field field =
                    MultipartFileListValidator.class.getDeclaredField("multipartFileValidator");
            field.setAccessible(true);

            validator.initialize(annotation);
            field.set(validator, mockFileValidator);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        MockMultipartFile file1 = new MockMultipartFile(
                "document1.pdf", "document1.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);
        MockMultipartFile file2 = new MockMultipartFile(
                "document2.pdf", "document2.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);

        List<MultipartFile> filesWithSameContent = Arrays.asList(file1, file2);

        // Act
        boolean isValid = validator.isValid(filesWithSameContent, context);

        // Assert
        assertTrue(isValid,
                "isValid should return true for a list with files having same content but different filenames");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(12)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with oversized file, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 1º item da lista está inválido.",
            "en_US|The item #1 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenOversizedFile_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB (exceeds the 5MB limit)
        MockMultipartFile largeFile = new MockMultipartFile(
                "large.pdf", "large.pdf", VALID_PDF_MIME_TYPE, largeContent);

        List<MultipartFile> filesWithLargeFile = Arrays.asList(VALID_PDF_FILE, largeFile);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = multipartFileListValidator.isValid(filesWithLargeFile, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with an oversized file");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(13)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with mix of files with and without filenames, then should return true")
    @Test
    void isValid_WhenMixOfFilesWithAndWithoutFilenames_ThenShouldReturnTrue() {
        // Arrange
        MultipartFileListValidator validator = new MultipartFileListValidator();
        MultipartFileValidation annotation = mock(MultipartFileValidation.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(annotation.maxSizeInMB()).thenReturn(5);
        when(annotation.maxFileCount()).thenReturn(3);

        MultipartFileValidator mockFileValidator = mock(MultipartFileValidator.class);
        when(mockFileValidator
                .isValid(any(MultipartFile.class), any(ConstraintValidatorContext.class))).thenReturn(true);

        try {
            java.lang.reflect.Field field =
                    MultipartFileListValidator.class.getDeclaredField("multipartFileValidator");
            field.setAccessible(true);

            validator.initialize(annotation);
            field.set(validator, mockFileValidator);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        MultipartFile fileWithNullFilename = mock(MultipartFile.class);
        when(fileWithNullFilename.getOriginalFilename()).thenReturn(null);

        List<MultipartFile> mixedFiles = Arrays.asList(VALID_PDF_FILE, fileWithNullFilename);

        // Act
        boolean isValid = validator.isValid(mixedFiles, context);

        // Assert
        assertTrue(isValid, "isValid should return true for a list with a mix of files with and without filenames");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(14)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with file having invalid MIME type, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 1º item da lista está inválido.",
            "en_US|The item #1 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFileWithInvalidMimeType_ThenShouldReturnFalse(String languageTag, String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MockMultipartFile fileWithInvalidMimeType = new MockMultipartFile(
                "document.xyz", "document.xyz", "application/xyz", "Invalid MIME type".getBytes());

        List<MultipartFile> filesWithInvalidMimeType = Arrays.asList(VALID_PDF_FILE, fileWithInvalidMimeType);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = multipartFileListValidator.isValid(filesWithInvalidMimeType, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid, "isValid should return false for a list with a file having invalid MIME type");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(15)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with files having same filename but different content, then should return false")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|O 1º item da lista está inválido.",
            "en_US|The item #1 in the list is invalid."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenFilesWithSameFilenameButDifferentContent_ThenShouldReturnFalse(String languageTag,
                                                                                    String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
                "document.pdf", "document.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);
        MockMultipartFile file2 = new MockMultipartFile(
                "document.pdf", "document.pdf", VALID_PDF_MIME_TYPE, "Different content".getBytes());

        List<MultipartFile> filesWithSameFilename = Arrays.asList(file1, file2);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = multipartFileListValidator.isValid(filesWithSameFilename, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid,
                "isValid should return false for a list with files having same filename but different content");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#isValid(List, ConstraintValidatorContext)}
     */
    @Order(16)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a list with duplicate filenames, then should return false with correct message")
    @ParameterizedTest(name = "Test {index} => locale={0} | expectedMessage={1}")
    @CsvSource(value = {
            "pt_BR|A lista não deve conter arquivos idênticos. Envie apenas arquivos únicos.",
            "en_US|The list must not contain identical files. Please send only unique files."
    }, delimiter = CSV_DELIMITER)
    void isValid_WhenDuplicateFilenames_ThenShouldReturnFalseWithCorrectMessage(String languageTag,
                                                                                String expectedMessage) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(languageTag.replace('_', '-')));

        // Arrange
        MultipartFileListValidator validator = new MultipartFileListValidator();
        MultipartFileValidation annotation = mock(MultipartFileValidation.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(annotation.maxSizeInMB()).thenReturn(5);
        when(annotation.maxFileCount()).thenReturn(3);

        MultipartFileValidator mockFileValidator = mock(MultipartFileValidator.class);
        when(mockFileValidator
                .isValid(any(MultipartFile.class), any(ConstraintValidatorContext.class))).thenReturn(true);

        try {
            java.lang.reflect.Field field =
                    MultipartFileListValidator.class.getDeclaredField("multipartFileValidator");
            field.setAccessible(true);

            validator.initialize(annotation);
            field.set(validator, mockFileValidator);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        MockMultipartFile file1 = new MockMultipartFile(
                "document.pdf", "document.pdf", VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT);
        MockMultipartFile file2 = new MockMultipartFile(
                "document.pdf", "document.pdf", VALID_PDF_MIME_TYPE, "Different content".getBytes());

        List<MultipartFile> filesWithSameFilename = Arrays.asList(file1, file2);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        // Act
        boolean isValid = validator.isValid(filesWithSameFilename, context);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(messageCaptor.capture());
        String capturedMessage = messageCaptor.getValue();

        // Assert
        assertEquals(expectedMessage, capturedMessage);
        assertFalse(isValid,
                "isValid should return false for a list with files having duplicate filenames");
    }

    /**
     * Method test for
     * {@link MultipartFileListValidator#initialize(MultipartFileValidation)}
     */
    @Order(17)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given a negative maxFileCount, then should use default value")
    @Test
    void initialize_WhenNegativeMaxFileCount_ThenShouldUseDefaultValue() {
        // Arrange
        MultipartFileValidation multipartFileValidation = mock(MultipartFileValidation.class);
        when(multipartFileValidation.allowedTypes())
                .thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(multipartFileValidation.maxSizeInMB()).thenReturn(5);
        when(multipartFileValidation.maxFileCount()).thenReturn(-1);

        MultipartFileListValidator validator = new MultipartFileListValidator();
        validator.initialize(multipartFileValidation);

        List<MultipartFile> manyFiles = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            manyFiles.add(new MockMultipartFile(
                    "file" + i + ".pdf",
                    "file" + i + ".pdf",
                    VALID_PDF_MIME_TYPE, VALID_PDF_CONTENT));
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
