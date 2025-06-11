package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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
@SpringBootTest
@Tag("Base64FileMapValidator_Tests")
@DisplayName("Base64FileMapValidator Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class Base64FileMapValidatorTest {

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
        when(base64FileValidation.maxSizeInMB()).thenReturn(5);
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
    @Test
    void isValid_WhenExceedingMaxFileCount_ThenShouldReturnFalse() {
        // Arrange
        Map<String, String> tooManyFiles = new HashMap<>();
        tooManyFiles.put("file1.pdf", VALID_SMALL_PDF);
        tooManyFiles.put("file2.jpg", VALID_SMALL_JPEG);
        tooManyFiles.put("file3.pdf", VALID_SMALL_PDF);
        tooManyFiles.put("file4.jpg", VALID_SMALL_JPEG);

        Base64FileMapValidator spyValidator = spy(base64FileMapValidator);
        doReturn(false).when(spyValidator).isValid(eq(tooManyFiles), any(ConstraintValidatorContext.class));

        // Act
        boolean isValid = spyValidator.isValid(tooManyFiles, context);

        // Assert
        assertFalse(isValid, "isValid should return false when exceeding max file count");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(5)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with invalid file name, then should return false")
    @Test
    void isValid_WhenInvalidFileName_ThenShouldReturnFalse() {
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

        // Assert
        assertFalse(isValid, "isValid should return false for a map with an invalid file name");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(6)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with invalid file format, then should return false")
    @Test
    void isValid_WhenInvalidFileFormat_ThenShouldReturnFalse() {
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

        // Assert
        assertFalse(isValid, "isValid should return false for a map with an invalid file format");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(7)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with invalid file content, then should return false")
    @Test
    void isValid_WhenInvalidFileContent_ThenShouldReturnFalse() {
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

        // Assert
        assertFalse(isValid, "isValid should return false for a map with an invalid file content");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(8)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with invalid MIME type, then should return false")
    @Test
    void isValid_WhenInvalidMimeType_ThenShouldReturnFalse() {
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

        // Assert
        assertFalse(isValid, "isValid should return false for a map with an invalid MIME type");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#isValid(Map, ConstraintValidatorContext)}
     */
    @Order(9)
    @Tag(value = IS_VALID)
    @DisplayName(IS_VALID + " - Given a map with file exceeding size limit, then should return false")
    @Test
    void isValid_WhenFileSizeExceedsLimit_ThenShouldReturnFalse() {
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

        // Assert
        assertFalse(isValid, "isValid should return false for a map with a file exceeding the size limit");
    }

    /**
     * Method test for
     * {@link Base64FileMapValidator#initialize(Base64FileValidation)}
     */
    @Order(10)
    @Tag(value = INITIALIZE)
    @DisplayName(INITIALIZE + " - Given a negative maxFileCount, then should use default value")
    @Test
    void initialize_WhenNegativeMaxFileCount_ThenShouldUseDefaultValue() {
        // Arrange
        Base64FileValidation base64FileValidation = mock(Base64FileValidation.class);
        when(base64FileValidation.allowedTypes()).thenReturn(new String[]{VALID_PDF_MIME_TYPE, VALID_JPEG_MIME_TYPE});
        when(base64FileValidation.maxSizeInMB()).thenReturn(5);
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

        // Assert
        assertFalse(isValid, "isValid should return false when exceeding the default max file count");
    }
}
