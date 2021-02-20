package com.ddmukhin.library.validation.errors;

public class NotBlankError extends SimpleValidationError {
    public NotBlankError(String path, String failedValue) {
        super("must not be blank", path, failedValue);
    }
}
