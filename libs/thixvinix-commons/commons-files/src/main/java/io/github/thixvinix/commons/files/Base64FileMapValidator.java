package io.github.thixvinix.commons.files;

import io.github.thixvinix.commons.validation.AbstractMapValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

/**
 * Validator for {@link Base64FileValidation} on a {@code Map<String, String>} field, where the
 * key is a filename and the value is its base64 content: validates map size/total size, and for
 * each entry the filename presence/format, content presence/validity, extension support, and
 * extension-vs-detected-MIME-type consistency.
 */
@Slf4j
public class Base64FileMapValidator
        extends AbstractMapValidator implements ConstraintValidator<Base64FileValidation, Map<String, String>> {

    /**
     * <p><strong>Regex for file name validation:</strong></p>
     * <ul>
     *   <li><strong>1.</strong> The file name cannot start with a dot: <code>^(?!\\.)</code>.</li>
     *   <li><strong>2.</strong> Must contain exactly one dot to separate filename and extension.</li>
     *   <li><strong>3.</strong> Must contain only letters, digits, underscores and hyphens.</li>
     * </ul>
     */
    private static final Pattern VALID_FILE_NAME_PATTERN = Pattern.compile("^(?!\\.)[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]+$");

    private Base64FileCollectionValidatorHelper helper;

    @Override
    public void initialize(Base64FileValidation annotation) {
        this.helper = new Base64FileCollectionValidatorHelper();
        this.helper.initialize(annotation);
    }

    @Override
    public boolean isValid(Map<String, String> values, ConstraintValidatorContext context) {
        if (isNull(values) || values.isEmpty()) {
            return true;
        }

        return !hasValidationErrors(values, context);
    }

    private boolean hasValidationErrors(Map<String, String> values, ConstraintValidatorContext context) {
        if (validateMaxSize(values, helper.getMaxFileCount(), context, FileMessageKeys.BASE64_MAX_FILE_COUNT)
                || validateTotalSize(values, helper.getMaxTotalSizeInMB(), helper::calculateBase64FileSize, context,
                FileMessageKeys.BASE64_MAX_TOTAL_SIZE)) {
            return true;
        }

        Set<String> uniqueBase64Files = new HashSet<>();

        int i = 0;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (!validateFileEntry(entry.getKey(), entry.getValue(), uniqueBase64Files, i++, context)) {
                return true;
            }
        }

        return false;
    }

    private boolean validateFileEntry(String fileName, String base64File, Set<String> uniqueBase64Files,
                                      int index, ConstraintValidatorContext context) {

        if (!validateFileNamePresence(fileName, index, context)
                || !validateBase64ContentPresence(fileName, base64File, index, context)
                || !validateFileNameFormat(fileName, index, context)
                || !validateBase64Content(base64File, index, context)) {
            return false;
        }

        String mimeType = extractMimeTypeFromBase64(base64File);
        String expectedExtension = getExtensionFromMimeType(mimeType);
        if (!validateMimeTypeSupported(expectedExtension, index, context)) {
            return false;
        }

        String fileExtension = extractExtensionFromFileName(fileName);
        return validateFileExtension(fileName, fileExtension, index, context)
                && validateExtensionMatchesMimeType(fileName, fileExtension, expectedExtension, index, context)
                && validateUniqueContent(base64File, uniqueBase64Files, context);
    }

    private boolean validateFileNamePresence(String fileName, int index, ConstraintValidatorContext context) {
        if (isNull(fileName) || fileName.trim().isEmpty()) {
            addConstraintViolation(context, FileMessageKeys.MISSING_FILENAME, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateBase64ContentPresence(String fileName, String base64File, int index,
                                                  ConstraintValidatorContext context) {
        if (isNull(base64File) || base64File.isBlank()) {
            addConstraintViolation(context, FileMessageKeys.MISSING_BASE64_CONTENT, fileName, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateFileNameFormat(String fileName, int index, ConstraintValidatorContext context) {
        if (!isFileNameValid(fileName)) {
            addConstraintViolation(context, FileMessageKeys.INVALID_FILENAME, fileName, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateBase64Content(String base64File, int index, ConstraintValidatorContext context) {
        if (!helper.validateIndividualBase64File(base64File, context)) {
            addConstraintViolation(context, FileMessageKeys.BASE64_INVALID_LIST_ITEM, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateMimeTypeSupported(String expectedExtension, int index, ConstraintValidatorContext context) {
        if (isNull(expectedExtension)) {
            addConstraintViolation(context, FileMessageKeys.UNSUPPORTED_FILETYPE, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateFileExtension(String fileName, String fileExtension, int index,
                                          ConstraintValidatorContext context) {
        if (MimeTypeEnum.isNotValidExtension(fileExtension)) {
            addConstraintViolation(context, FileMessageKeys.INVALID_EXTENSION,
                    fileName, fileExtension, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateExtensionMatchesMimeType(String fileName, String fileExtension,
                                                     String expectedExtension, int index,
                                                     ConstraintValidatorContext context) {
        if (!fileExtension.equalsIgnoreCase(expectedExtension)) {
            addConstraintViolation(context, FileMessageKeys.EXTENSION_MISMATCH,
                    fileName, fileExtension, expectedExtension, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateUniqueContent(String base64File, Set<String> uniqueBase64Files,
                                          ConstraintValidatorContext context) {
        if (!uniqueBase64Files.add(base64File)) {
            addConstraintViolation(context, FileMessageKeys.BASE64_DUPLICATE_FILE);
            return false;
        }
        return true;
    }

    private boolean isFileNameValid(String fileName) {
        if (isNull(fileName) || fileName.isBlank()) {
            return false;
        }

        return VALID_FILE_NAME_PATTERN.matcher(fileName).matches();
    }

    /**
     * Extracts the MIME type from a Base64 data-URI string (e.g. "data:image/png;base64,...").
     *
     * @return the extracted MIME type, or null if input is null or not a data URI
     */
    private String extractMimeTypeFromBase64(String base64File) {
        if (isNull(base64File) || !base64File.contains(";base64,")) {
            return null;
        }

        return base64File.split(";")[0].split(":")[1];
    }

    private String getExtensionFromMimeType(String mimeType) {
        return MimeTypeEnum.getExtensionFromMimeType(mimeType);
    }

    private String extractExtensionFromFileName(String fileName) {
        if (isNull(fileName) || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
