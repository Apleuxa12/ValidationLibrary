package com.ddmukhin.validation.errors;

public class SizeError extends SimpleValidationError{

    public SizeError(int min, int max, String path, Object failedValue) {
        super(String.format("must be in range between %d and %d", min, max), path, failedValue);
    }
}
