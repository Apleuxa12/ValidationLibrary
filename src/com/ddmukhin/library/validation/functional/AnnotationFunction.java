package com.ddmukhin.library.validation.functional;

import com.ddmukhin.library.validation.errors.ValidationError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public interface AnnotationFunction {

    Optional<ValidationError> validate(Annotation annotationInstance, Object object, String path);

}
