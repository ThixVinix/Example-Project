package io.github.thixvinix.commons.documents;

/**
 * Message keys owned by this module, kept as constants so a completeness test can assert every
 * key exists in {@code messages.properties}, {@code messages_en.properties} and
 * {@code messages_pt_BR.properties} — the mechanism that turns a typo'd key from a silently
 * unresolved message into a build failure.
 */
public final class DocumentMessageKeys {

    public static final String CPFCNPJ_INVALID = "commons.validation.document.cpfcnpj.invalid";
    public static final String CPFCNPJ_INVALID_LENGTH = "commons.validation.document.cpfcnpj.invalidLength";
    public static final String CPF_INVALID_CHECK_DIGIT = "commons.validation.document.cpf.invalidCheckDigit";
    public static final String CNPJ_INVALID_CHECK_DIGIT = "commons.validation.document.cnpj.invalidCheckDigit";

    private DocumentMessageKeys() {
    }
}
