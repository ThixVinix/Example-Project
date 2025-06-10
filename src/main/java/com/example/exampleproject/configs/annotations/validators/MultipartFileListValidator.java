package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.MultipartFileValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validator to check if a list of MultipartFile objects is valid, according to specified constraints.
 * This validator checks:
 * 1. The maximum number of files is allowed;
 * 2. That there are no duplicate files;
 * 3. That each file is valid, according to the MultipartFileValidator.
 */
@Slf4j
public class MultipartFileListValidator implements ConstraintValidator<MultipartFileValidation, List<MultipartFile>> {

    private MultipartFileValidator multipartFileValidator;
    private int maxFileCount;

    @Override
    public void initialize(MultipartFileValidation annotation) {
        this.maxFileCount = annotation.maxFileCount();
        multipartFileValidator = new MultipartFileValidator();
        multipartFileValidator.initialize(annotation);
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (isNullOrEmpty(files)) {
            return true;
        }

        if (files.size() > maxFileCount) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            MessageUtils.getMessage(
                                    "msg.validation.request.field.multipartfile.max.file.count", maxFileCount)
                    )
                    .addConstraintViolation();
            return false;
        }

        Set<String> uniqueFileNames = new HashSet<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            
            // Skip null files
            if (file == null) {
                continue;
            }
            
            // Check for duplicate files by original filename
            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.isEmpty() && !uniqueFileNames.add(fileName)) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.multipartfile.duplicate.file")
                        )
                        .addConstraintViolation();
                return false;
            }

            // Validate each file using the MultipartFileValidator
            if (!multipartFileValidator.isValid(file, context)) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.multipartfile.invalid.list",
                                        i + 1)
                        )
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private boolean isNullOrEmpty(List<MultipartFile> files) {
        return files == null || files.isEmpty();
    }
}