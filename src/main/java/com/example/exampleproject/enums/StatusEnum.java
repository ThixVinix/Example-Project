package com.example.exampleproject.enums;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Optional;

@Getter
@ToString
@AllArgsConstructor
public enum StatusEnum {

    PENDING(5, "PENDENTE"),
    ACTIVE(1, "ATIVO"),
    INACTIVE(0, "INATIVO");

    private final int code;
    private final String value;

    /**
     * Retrieves a {@link StatusEnum} instance based on its string literal name.
     *
     * @param name the name of the enum constant (case-insensitive)
     * @return an {@link Optional} containing the matching {@link StatusEnum} instance,
     * or {@link Optional#empty()} if no match is found.
     */
    public static Optional<StatusEnum> fromLiteralName(String name) {
        return Arrays.stream(StatusEnum.values())
                .filter(statusEnum -> statusEnum.name().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Retrieves a {@link StatusEnum} instance based on its numeric code.
     *
     * @param code the numeric code to match
     * @return an {@link Optional} containing the matching {@link StatusEnum} instance,
     * or {@link Optional#empty()} if no match is found.
     */
    public static Optional<StatusEnum> fromCode(int code) {
        return Arrays.stream(StatusEnum.values())
                .filter(statusEnum -> statusEnum.code == code)
                .findFirst();
    }

    /**
     * Retrieves a {@link StatusEnum} instance based on its string value.
     *
     * @param value the string value to match (case-insensitive)
     * @return an {@link Optional} containing the matching {@link StatusEnum} instance,
     * or {@link Optional#empty()} if no match is found.
     */
    public static Optional<StatusEnum> fromValue(String value) {
        return Arrays.stream(StatusEnum.values())
                .filter(statusEnum -> statusEnum.value.equalsIgnoreCase(value))
                .findFirst();
    }

    /**
     * Retrieves a {@link StatusEnum} instance based on its string literal name.
     * Throws a {@link BusinessException} if no match is found.
     *
     * @param name the name of the enum constant (case-insensitive)
     * @return the corresponding {@link StatusEnum} instance
     * @throws BusinessException if no {@link StatusEnum} constant matches the provided name.
     */
    public static StatusEnum fromLiteralNameOrThrow(String name) {
        return fromLiteralName(name)
                .orElseThrow(() -> new BusinessException("Invalid StatusEnum name: " + name));
    }

    /**
     * Retrieves a {@link StatusEnum} instance based on its numeric code.
     * Throws a {@link BusinessException} if no match is found.
     *
     * @param code the numeric code to match
     * @return the corresponding {@link StatusEnum} instance
     * @throws BusinessException if no {@link StatusEnum} constant matches the provided code.
     */
    public static StatusEnum fromCodeOrThrow(int code) {
        return fromCode(code)
                .orElseThrow(() -> new BusinessException("Invalid StatusEnum code: " + code));
    }

    /**
     * Retrieves a {@link StatusEnum} instance based on its string value.
     * Throws a {@link BusinessException} if no match is found.
     *
     * @param value the string value to match (case-insensitive)
     * @return the corresponding {@link StatusEnum} instance
     * @throws BusinessException if no {@link StatusEnum} constant matches the provided value.
     */
    public static StatusEnum fromValueOrThrow(String value) {
        return fromValue(value)
                .orElseThrow(() -> new BusinessException("Invalid StatusEnum value: " + value));
    }
}