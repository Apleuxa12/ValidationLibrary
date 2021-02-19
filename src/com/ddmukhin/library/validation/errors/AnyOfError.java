package com.ddmukhin.library.validation.errors;

import java.util.List;
import java.util.stream.Collectors;

public class AnyOfError extends SimpleValidationError {

    public AnyOfError(List<String> values, String path, Object failedValue) {
        super("must be one of " + values.stream().map(x -> "'" + x + "'").collect(Collectors.joining(", ")),
                path, failedValue);
    }
}
