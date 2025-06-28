package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.configs.annotations.enums.MimeTypeEnum;
import com.example.exampleproject.configs.annotations.validators.base.AbstractValidator;
import com.example.exampleproject.configs.annotations.validators.helpers.Base64FileCollectionValidatorHelper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public class Base64FileMapValidator
        extends AbstractValidator implements ConstraintValidator<Base64FileValidation, Map<String, String>> {

    /**
     * <p><strong>Regex for file name validation:</strong></p>
     * <ul>
     *   <li><strong>1.</strong> The file name cannot start with a dot: <code>^(?!\\.)</code>.</li>
     *   <li><strong>2.</strong> Must contain exactly one dot to separate filename and extension.</li>
     *   <li><strong>3.</strong> Must contain only the following characters:
     *       <ul>
     *           <li>Letters: <code>a-z</code> or <code>A-Z</code>,</li>
     *           <li>Digits: <code>0-9</code>,</li>
     *           <li>Underscores: <code>_</code>,</li>
     *           <li>Hyphens: <code>-</code>.</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * <p><strong>Examples:</strong></p>
     * <ul>
     *   <li><strong>Valid:</strong>
     *   <code>document.pdf</code>, <code>123-file_name.jpg</code>, <code>backup.txt</code>.</li>
     *   <li><strong>Invalid:</strong>
     *   <code>.hidden</code>, <code>file</code>, <code>file.</code>, <code>invalid@name.pdf</code>,
     *   <code>file.name.pdf</code>.</li>
     * </ul>
     */
    private static final String VALID_FILE_NAME_REGEX = "^(?!\\.)[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]+$";

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

        if (values.size() > helper.getMaxFileCount()) {
            addConstraintViolation(context, "msg.validation.request.field.base64file.max.file.count", 
                    String.valueOf(helper.getMaxFileCount()));
            return false;
        }

        if (!validateTotalSize(values, context)) {
            return false;
        }

        Set<String> uniqueBase64Files = new HashSet<>();
        int i = 0;

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String fileName = entry.getKey();
            String base64File = entry.getValue();

            if (!validateFileEntry(fileName, base64File, uniqueBase64Files, i, context)) {
                return false;
            }

            i++;
        }

        return true;
    }

    /**
     * Validates a single file entry in the map.
     *
     * @param fileName          The file name (key in the map)
     * @param base64File        The base64 content (value in the map)
     * @param uniqueBase64Files Set to track unique base64 contents
     * @param index             The index of the current entry for error messages
     * @param context           The validation context
     * @return true if the entry is valid, false otherwise
     */
    private boolean validateFileEntry(String fileName, String base64File, Set<String> uniqueBase64Files,
                                      int index, ConstraintValidatorContext context) {

        if (!validateFileNamePresence(fileName, index, context)) {
            return false;
        }

        if (!validateBase64ContentPresence(fileName, base64File, index, context)) {
            return false;
        }

        if (!validateFileNameFormat(fileName, index, context)) {
            return false;
        }

        if (!validateBase64Content(base64File, index, context)) {
            return false;
        }

        String mimeType = extractMimeTypeFromBase64(base64File);
        String expectedExtension = getExtensionFromMimeType(mimeType);
        if (!validateMimeTypeSupported(expectedExtension, index, context)) {
            return false;
        }

        String fileExtension = extractExtensionFromFileName(fileName);
        if (!validateFileExtension(fileName, fileExtension, index, context)) {
            return false;
        }

        if (!validateExtensionMatchesMimeType(fileName, fileExtension, expectedExtension, index, context)) {
            return false;
        }

        return validateUniqueContent(base64File, uniqueBase64Files, context);
    }

    private boolean validateFileNamePresence(String fileName, int index, ConstraintValidatorContext context) {
        if (isNull(fileName) || fileName.trim().isEmpty()) {
            addConstraintViolation(context, "msg.validation.request.field.missing.filename", 
                    String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateBase64ContentPresence(String fileName, String base64File, int index,
                                                  ConstraintValidatorContext context) {
        if (isNull(base64File) || base64File.isBlank()) {
            addConstraintViolation(context, "msg.validation.request.field.missing.base64content",
                    fileName, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateFileNameFormat(String fileName, int index, ConstraintValidatorContext context) {
        if (!isFileNameValid(fileName)) {
            addConstraintViolation(context, "msg.validation.request.field.invalid.filename",
                    fileName, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateBase64Content(String base64File, int index, ConstraintValidatorContext context) {
        if (!helper.validateIndividualBase64File(base64File, context)) {
            addConstraintViolation(context, "msg.validation.request.field.base64file.invalid.list", 
                    String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateMimeTypeSupported(String expectedExtension, int index, ConstraintValidatorContext context) {
        if (isNull(expectedExtension)) {
            addConstraintViolation(context, "msg.validation.request.field.unsupported.filetype", 
                    String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateFileExtension(String fileName, String fileExtension, int index,
                                          ConstraintValidatorContext context) {
        if (MimeTypeEnum.isNotValidExtension(fileExtension)) {
            addConstraintViolation(context, "msg.validation.request.field.invalid.extension",
                    fileName, fileExtension, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateExtensionMatchesMimeType(String fileName, String fileExtension,
                                                     String expectedExtension, int index,
                                                     ConstraintValidatorContext context) {
        if (!fileExtension.equalsIgnoreCase(expectedExtension)) {
            addConstraintViolation(context, "msg.validation.request.field.extension.mismatch",
                    fileName, fileExtension, expectedExtension, String.valueOf(index + 1));
            return false;
        }
        return true;
    }

    private boolean validateUniqueContent(String base64File, Set<String> uniqueBase64Files,
                                          ConstraintValidatorContext context) {
        if (!uniqueBase64Files.add(base64File)) {
            addConstraintViolation(context, "msg.validation.request.field.base64file.duplicate.file");
            return false;
        }
        return true;
    }

    /**
     * Validates whether the given file name is valid based on a specific set of rules.
     *
     * @param fileName the file name to be validated; it can be null or empty.
     * @return true if the file name is non-null, non-blank, and matches the specific file name criteria,
     * otherwise false.
     */
    private boolean isFileNameValid(String fileName) {
        if (isNull(fileName) || fileName.isBlank()) {
            return false;
        }

        return fileName.matches(VALID_FILE_NAME_REGEX);
    }

    /**
     * Extracts the MIME type from a Base64-encoded string, provided the input string
     * contains a valid "data" URI format with a MIME type prefix.
     *
     * @param base64File the Base64-encoded string, expected to start with a "data" URI
     *                   containing a MIME type (e.g., "data:image/png;base64,...").
     *                   If null or improperly formatted, this method returns null.
     * @return the extracted MIME type as a string (e.g., "image/png") if the input is valid;
     * null if the input is null or does not contain a valid MIME type.
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

    /**
     * Extracts the file extension from a filename.
     *
     * @param fileName the filename including extension
     * @return the extension part of the filename
     */
    private String extractExtensionFromFileName(String fileName) {
        if (isNull(fileName) || !fileName.contains(".")) {
            return StringUtils.EMPTY;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Validates that the total size of all files does not exceed the maximum total size.
     *
     * @param values the map of files to validate
     * @param context the validation context
     * @return true if the total size is valid, false otherwise
     */
    private boolean validateTotalSize(Map<String, String> values, ConstraintValidatorContext context) {
        if (helper.getMaxTotalSizeInMB() <= NumberUtils.INTEGER_ZERO) {
            return true;
        }

        final long BYTES_IN_ONE_MB = 1024L * 1024L;
        long maxTotalSizeInBytes = helper.getMaxTotalSizeInMB() * BYTES_IN_ONE_MB;
        long totalSizeInBytes = NumberUtils.LONG_ZERO;

        for (String base64Value : values.values()) {
            if (nonNull(base64Value)) {
                long fileSize = helper.calculateBase64FileSize(base64Value);
                totalSizeInBytes += fileSize;
            }
        }

        if (totalSizeInBytes > maxTotalSizeInBytes) {
            double actualTotalSizeInMB = (double) totalSizeInBytes / BYTES_IN_ONE_MB;
            addConstraintViolation(context, "msg.validation.request.field.base64file.max.total.size",
                    String.format("%.4f", actualTotalSizeInMB), String.valueOf(helper.getMaxTotalSizeInMB()));
            return false;
        }
        return true;
    }

}
