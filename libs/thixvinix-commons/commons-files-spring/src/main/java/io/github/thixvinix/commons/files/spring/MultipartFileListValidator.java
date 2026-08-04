package io.github.thixvinix.commons.files.spring;

import io.github.thixvinix.commons.validation.AbstractListValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Validator for {@link MultipartFileValidation} on a {@code List<MultipartFile>}: validates list
 * size, total size, duplicate filenames, and each item individually.
 */
@Slf4j
public class MultipartFileListValidator
        extends AbstractListValidator implements ConstraintValidator<MultipartFileValidation, List<MultipartFile>> {

    private MultipartFileValidator multipartFileValidator;
    private int maxFileCount;
    private int maxTotalSizeMB;

    @Override
    public void initialize(MultipartFileValidation annotation) {
        this.maxFileCount = annotation.maxFileCount();
        this.maxTotalSizeMB = annotation.maxTotalSizeMB();
        multipartFileValidator = new MultipartFileValidator();
        multipartFileValidator.initialize(annotation);
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (isNullOrEmpty(files)) {
            return true;
        }

        return !hasValidationErrors(files, context);
    }

    private boolean hasValidationErrors(List<MultipartFile> files, ConstraintValidatorContext context) {
        return validateMaxSize(files, maxFileCount, context, MultipartFileMessageKeys.MAX_FILE_COUNT)
                || validateTotalSize(files, maxTotalSizeMB, this::calculateMultipartFileSize, context,
                MultipartFileMessageKeys.MAX_TOTAL_SIZE)
                || hasInvalidItem(files, this::validateMultipartFile, context,
                MultipartFileMessageKeys.INVALID_LIST_ITEM)
                || hasDuplicateFileNames(files, context);
    }

    private boolean validateMultipartFile(MultipartFile file, ConstraintValidatorContext context) {
        return isNull(file) || multipartFileValidator.isValid(file, context);
    }

    private boolean hasDuplicateFileNames(List<MultipartFile> files, ConstraintValidatorContext context) {
        Set<String> uniqueFileNames = new HashSet<>();

        for (MultipartFile file : files) {
            if (isNull(file)) {
                continue;
            }

            String fileName = file.getOriginalFilename();
            if (nonNull(fileName) && !fileName.isEmpty() && !uniqueFileNames.add(fileName)) {
                addConstraintViolation(context, MultipartFileMessageKeys.DUPLICATE_FILE);
                return true;
            }
        }

        return false;
    }

    private long calculateMultipartFileSize(MultipartFile file) {
        if (isNull(file) || file.isEmpty()) {
            return 0L;
        }
        return file.getSize();
    }
}
