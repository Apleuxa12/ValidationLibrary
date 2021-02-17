package com.ddmukhin.validation;

import com.ddmukhin.validation.errors.ValidationError;

import java.util.Set;

public interface Validator {

    Set<ValidationError> validate(Object object) throws IllegalAccessException;

}
