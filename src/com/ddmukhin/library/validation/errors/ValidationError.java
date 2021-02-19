package com.ddmukhin.library.validation.errors;

public interface ValidationError {

    String getMessage();

    String getPath();

    Object getFailedValue();
}
