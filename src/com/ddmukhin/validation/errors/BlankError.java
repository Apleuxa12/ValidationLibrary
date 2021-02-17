package com.ddmukhin.validation.errors;

public class BlankError extends SimpleValidationError {
    public BlankError(String path) {
        super("must not be blank", path, "\"\"");
    }
}
