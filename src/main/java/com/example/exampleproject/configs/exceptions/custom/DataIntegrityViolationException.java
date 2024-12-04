package com.example.exampleproject.configs.exceptions.custom;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DataIntegrityViolationException extends RuntimeException {

    public DataIntegrityViolationException(String message) {
        super(message);
    }
}
