package com.ddmukhin.validation.errors;

public class InRangeError extends SimpleValidationError {

    public InRangeError(long min, long max, String path, Object failedValue) {
        super(String.format("must be in range between %d and %d", min, max), path, failedValue);
    }
}
