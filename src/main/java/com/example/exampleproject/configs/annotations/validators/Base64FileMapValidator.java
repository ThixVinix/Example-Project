package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.configs.annotations.enums.MimeTypeEnum;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Base64FileMapValidator implements ConstraintValidator<Base64FileValidation, Map<String, String>> {

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
     *   <code>.hidden</code>, <code>file</code>, <code>file.</code>, <code>invalid@name.pdf</code>, <code>file.name.pdf</code>.</li>
     * </ul>
     */
    private static final String VALID_FILE_NAME_REGEX = "^(?!\\.)[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]+$";

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
            String fileName = entry.getKey();
            String base64File = entry.getValue();

            // 1. Verifique se `fileName` está preenchido
            if (fileName == null || fileName.trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage(
                                        "msg.validation.request.field.missing.filename",
                                        i + 1)
                        )
                        .addConstraintViolation();
                return false;
            }

            // 2. Verifique se `base64File` está preenchido
            if (base64File == null || base64File.isBlank()) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage(
                                        "msg.validation.request.field.missing.base64content",
                                        fileName,
                                        i + 1)
                        )
                        // .addPropertyNode(fileName)
                        .addConstraintViolation();
                return false;
            }

            // 3. Valida se o nome do arquivo possui formato válido (nome.extensão)
            if (!isFileNameValid(fileName)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage(
                                        "msg.validation.request.field.invalid.filename",
                                        fileName,
                                        i + 1)
                        )
                        //.addPropertyNode(fileName)
                        .addConstraintViolation();
                return false;
            }

            // 4. Valida o conteúdo Base64 reutilizando `Base64FileValidator`
            if (!base64FileValidator.isValid(base64File, context)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage(
                                        "msg.validation.request.field.base64file.invalid.list", i + 1))
                        .addConstraintViolation();
                return false;
            }

            // 5. Extrai o tipo MIME do Base64 e gera a extensão correspondente
            String mimeType = extractMimeTypeFromBase64(base64File);
            String expectedExtension = getExtensionFromMimeType(mimeType);

            if (Objects.isNull(expectedExtension)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage(
                                        "msg.validation.request.field.unsupported.filetype",
                                        i + 1)
                        )
                        //.addPropertyNode(fileName)
                        .addConstraintViolation();
                return false;
            }

            // 6. Extrai a extensão do nome do arquivo e verifica se corresponde ao tipo MIME
            String fileExtension = extractExtensionFromFileName(fileName);
            if (!fileExtension.equalsIgnoreCase(expectedExtension)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage(
                                        "msg.validation.request.field.extension.mismatch",
                                        fileName,
                                        fileExtension,
                                        expectedExtension,
                                        i + 1)
                        )
                        .addConstraintViolation();
                return false;
            }

            // 7. Verifica duplicatas no conteúdo Base64
            if (!uniqueBase64Files.add(base64File)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.base64file.duplicate.file")
                        )
                        .addConstraintViolation();
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

    /**
     * Extracts the file extension from a filename.
     *
     * @param fileName the filename including extension
     * @return the extension part of the filename
     */
    private String extractExtensionFromFileName(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return StringUtils.EMPTY;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
