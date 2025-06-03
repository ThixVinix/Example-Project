package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.configs.annotations.enums.MimeTypeEnum;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Base64FileMapValidator implements ConstraintValidator<Base64FileValidation, Map<String, String>> {

    /**
     * <p><strong>Regex for file name validation:</strong></p>
     * <ul>
     *   <li><strong>1.</strong> The file name cannot start with a dot: <code>^(?!\\.)</code>.</li>
     *   <li><strong>2.</strong> Cannot contain consecutive dots: <code>(?!.*\\.\\.)</code>.</li>
     *   <li><strong>3.</strong> Must contain only the following characters:
     *       <ul>
     *           <li>Letters: <code>a-z</code> or <code>A-Z</code>,</li>
     *           <li>Digits: <code>0-9</code>,</li>
     *           <li>Underscores: <code>_</code>,</li>
     *           <li>Hyphens: <code>-</code></li>
     *       </ul>
     *       before an optional dot: <code>[a-zA-Z0-9_-]+</code>.
     *   </li>
     *   <li><strong>4.</strong> May contain a dot (<code>.</code>), followed by valid characters
     *       for an extension: <code>(\\.[a-zA-Z0-9_-]+)?</code>.
     *   </li>
     *   <li><strong>5.</strong> Cannot end with a dot and must adhere to the full pattern: <code>$</code>.</li>
     * </ul>
     *
     * <p><strong>Examples:</strong></p>
     * <ul>
     *   <li><strong>Valid:</strong>
     *   <code>document.txt</code>, <code>123-file_name.log</code>, <code>backup</code>.</li>
     *   <li><strong>Invalid:</strong>
     *   <code>.hidden</code>, <code>file..name</code>, <code>file.</code>, <code>invalid@name.txt</code>.</li>
     * </ul>
     */
    private static final String VALID_FILE_NAME_REGEX = "^(?!\\.)(?!.*\\.\\.)[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)?$";


    private Base64FileValidator base64FileValidator;

    @Override
    public void initialize(Base64FileValidation annotation) {
        base64FileValidator = new Base64FileValidator();
        base64FileValidator.initialize(annotation);
    }

    @Override
    public boolean isValid(Map<String, String> values, ConstraintValidatorContext context) {
        if (values == null || values.isEmpty()) {
            return true; // Mapa vazio ou nulo é considerado válido
        }

        Set<String> uniqueBase64Files = new HashSet<>(); // Para verificar duplicatas por conteúdo Base64
        int i = 0;

        for (Map.Entry<String, String> entry : values.entrySet()) {
            String fileNameWithoutExtension = entry.getKey();
            String base64File = entry.getValue();

            // 1. Verifique se `fileNameWithoutExtension` está preenchido
            if (fileNameWithoutExtension == null || fileNameWithoutExtension.isBlank()) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.missing.filename")
                        )
                        .addConstraintViolation();
                return false;
            }

            // 2. Verifique se `base64File` está preenchido
            if (base64File == null || base64File.isBlank()) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.missing.base64content", fileNameWithoutExtension)
                        )
                        .addPropertyNode(fileNameWithoutExtension).addConstraintViolation();
                return false;
            }

            // 3. Remove extensões incluídas indevidamente na chave
            if (fileNameWithoutExtension.contains(".")) {
                fileNameWithoutExtension = fileNameWithoutExtension.substring(0, fileNameWithoutExtension.indexOf('.'));
            }

            // 4. Valida se o nome do arquivo não possui caracteres ilegais
            if (!isFileNameValid(fileNameWithoutExtension)) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.invalid.filename", fileNameWithoutExtension)
                        )
                        .addPropertyNode(fileNameWithoutExtension).addConstraintViolation();
                return false;
            }

            // 5. Valida o conteúdo Base64 reutilizando `Base64FileValidator`
            if (!base64FileValidator.isValid(base64File, context)) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage(
                                        "msg.validation.request.field.base64file.invalid.list", i + 1))
                        .addConstraintViolation();
                return false;
            }

            // 6. Extrai o tipo MIME do Base64 e gera a extensão correspondente
            String mimeType = extractMimeTypeFromBase64(base64File);
            String expectedExtension = getExtensionFromMimeType(mimeType);

            if (expectedExtension == null) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.unsupported.filetype", fileNameWithoutExtension)
                        )
                        .addPropertyNode(fileNameWithoutExtension).addConstraintViolation();
                return false;
            }

            // Gera o nome completo do arquivo com extensão
            String completeFileName = fileNameWithoutExtension + "." + expectedExtension;

            // 7. Verifica duplicatas no conteúdo Base64
            if (!uniqueBase64Files.add(base64File)) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.duplicate.file", completeFileName)
                        )
                        .addPropertyNode(completeFileName).addConstraintViolation();
                return false;
            }
            i++;
        }

        return true; // Todos os arquivos são válidos
    }


    /**
     * Validates whether the given file name is valid based on a specific set of rules.
     *
     * @param fileName the file name to be validated; it can be null or empty.
     * @return true if the file name is non-null, non-blank, and matches the specific file name criteria,
     *         otherwise false.
     */
    private boolean isFileNameValid(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }

        return fileName.matches(VALID_FILE_NAME_REGEX);
    }

    /**
     * Extrai o MIME type do conteúdo Base64. Já validado no `Base64FileValidator`.
     *
     * @param base64File Conteúdo Base64.
     * @return O tipo MIME ou null se for inválido.
     */
    private String extractMimeTypeFromBase64(String base64File) {
        if (base64File == null || !base64File.contains(";base64,")) {
            return null;
        }
        // "data:image/png;base64,..." -> "image/png"
        return base64File.split(";")[0].split(":")[1];
    }

    private String getExtensionFromMimeType(String mimeType) {
        return MimeTypeEnum.getExtensionFromMimeType(mimeType);
    }

}