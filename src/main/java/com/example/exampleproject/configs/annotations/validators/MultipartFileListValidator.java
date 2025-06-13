package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.MultipartFileValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractListValidator;
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
public class MultipartFileListValidator
        extends AbstractListValidator implements ConstraintValidator<MultipartFileValidation, List<MultipartFile>> {

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

        if (validateMaxSize(files, maxFileCount, context,
                "msg.validation.request.field.multipartfile.max.file.count")) {
            return false;
        }

        if (!validateEachItem(files, this::validateMultipartFile, context, 
                "msg.validation.request.field.multipartfile.invalid.list")) {
            return false;
        }

        return validateUniqueFileNames(files, context);
    }

    private boolean validateMultipartFile(MultipartFile file, ConstraintValidatorContext context) {
        return file == null || multipartFileValidator.isValid(file, context);
    }

    private boolean validateUniqueFileNames(List<MultipartFile> files, ConstraintValidatorContext context) {
        Set<String> uniqueFileNames = new HashSet<>();

        for (MultipartFile file : files) {
            if (file == null) {
                continue;
            }

            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.isEmpty() && !uniqueFileNames.add(fileName)) {
                addConstraintViolation(context, "msg.validation.request.field.multipartfile.duplicate.file");
                return false;
            }
        }

        return true;
    }
}
