package com.ddmukhin.validation.errors;

public class PositiveError extends SimpleValidationError {

    public PositiveError(String path, Object failedValue) {
        super("must be greater than zero", path, failedValue);
    }
}
