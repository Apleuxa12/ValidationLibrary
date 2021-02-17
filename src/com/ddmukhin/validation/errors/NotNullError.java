package com.ddmukhin.validation.errors;

public class NotNullError extends SimpleValidationError{

    public NotNullError(String path) {
        super("must not be null", path, null);
    }
}
