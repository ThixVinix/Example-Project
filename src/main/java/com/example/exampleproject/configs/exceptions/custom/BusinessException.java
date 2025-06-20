package com.example.exampleproject.configs.exceptions.custom;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String fieldName;

    public BusinessException() {
        this.fieldName = "";
    }

    public BusinessException(String message) {
        super(message);
        this.fieldName = "";
    }


    public BusinessException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }
}
