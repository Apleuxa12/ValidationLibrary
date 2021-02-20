package com.ddmukhin.library.validation;

import com.ddmukhin.library.validation.errors.ValidationError;

import java.util.Set;

public interface Validator {

    Set<ValidationError> validate(Object object) throws IllegalAccessException;
}
