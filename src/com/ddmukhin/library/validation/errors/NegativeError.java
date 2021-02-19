package com.ddmukhin.library.validation.errors;

public class NegativeError extends SimpleValidationError {

    public NegativeError(String path, Object failedValue) {
        super("must be less than zero", path, failedValue);
    }
}
