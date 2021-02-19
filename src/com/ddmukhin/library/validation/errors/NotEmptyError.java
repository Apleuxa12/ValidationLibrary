package com.ddmukhin.library.validation.errors;

import java.util.List;

public class NotEmptyError extends SimpleValidationError {

    public NotEmptyError(String path) {
        super("must not be empty", path, List.of());
    }
}
