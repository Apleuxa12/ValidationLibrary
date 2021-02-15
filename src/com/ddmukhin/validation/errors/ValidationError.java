package com.ddmukhin.validation.errors;

public interface ValidationError {

    String getMessage();

    String getPath();

    Object getFailedValue();
}
