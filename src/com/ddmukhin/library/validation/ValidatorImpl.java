package com.ddmukhin.library.validation;

import com.ddmukhin.library.annotations.*;
import com.ddmukhin.library.validation.errors.*;
import com.ddmukhin.library.validation.functional.AnnotationFunction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import static com.ddmukhin.library.utils.AnnotationUtils.*;

public class ValidatorImpl implements Validator {

    private final Set<ValidationError> errors = new HashSet<>();

    private final Map<Class<? extends Annotation>, AnnotationFunction> functions = new HashMap<>();

    private void fillFunctions() {
        functions.put(AnyOf.class, (annotationInstance, field, object, path) -> {
            AnyOf annotation = (AnyOf) annotationInstance;

            if(!object.getClass().equals(String.class))
                return Optional.empty();

            final String string = (String) object;

            if (Arrays.asList(annotation.value()).contains(string))
                return Optional.empty();
            else
                return Optional.of(new AnyOfError(Arrays.asList(annotation.value()), path, object));
        });

        functions.put(InRange.class, (annotationInstance, field, object, path) -> {
            InRange annotation = (InRange) annotationInstance;

            if (!Number.class.isAssignableFrom(object.getClass()))
                return Optional.empty();

            final Number number = (Number) object;

            if ((number.longValue() < annotation.min() || number.longValue() > annotation.max()))
                return Optional.of(new InRangeError(annotation.min(), annotation.max(), path, object));
            return Optional.empty();
        });

        functions.put(Negative.class, (annotationInstance, field, object, path) -> {
            if (!Number.class.isAssignableFrom(object.getClass()))
                return Optional.empty();

            final Number number = (Number) object;

            if (number.intValue() > 0)
                return Optional.of(new NegativeError(path, object));
            return Optional.empty();
        });

        functions.put(Positive.class, (annotationInstance, field, object, path) -> {
            if (!Number.class.isAssignableFrom(object.getClass()))
                return Optional.empty();

            final Number number = (Number) object;

            if (number.intValue() < 0)
                return Optional.of(new PositiveError(path, object));
            return Optional.empty();
        });

        functions.put(NotNull.class, (annotationInstance, field, object, path) -> {
            if (object == null)
                return Optional.of(new NotNullError(path));
            return Optional.empty();
        });

        functions.put(NotBlank.class, (annotationInstance, field, object, path) -> {
            if (!String.class.equals(object.getClass()))
                return Optional.empty();

            final String string = (String) object;

            if (string.isBlank())
                return Optional.of(new NotBlankError(path, string));
            return Optional.empty();
        });

        functions.put(Size.class, (annotationInstance, field, object, path) -> {
            Size annotation = (Size) annotationInstance;

            if (!Collection.class.isAssignableFrom(object.getClass()))
                return Optional.empty();

            final Collection<?> collection = (Collection<?>) object;

            if (collection.size() < annotation.min() || collection.size() > annotation.max())
                return Optional.of(new SizeError(annotation.min(), annotation.max(), path, collection.size()));
            return Optional.empty();
        });

        functions.put(NotEmpty.class, (annotationInstance, field, object, path) -> {
            if (!Collection.class.isAssignableFrom(object.getClass()))
                return Optional.empty();

            final Collection<?> collection = (Collection<?>) object;

            if (collection.size() == 0)
                return Optional.of(new NotEmptyError(path));
            return Optional.empty();
        });
    }

    @Override
    public Set<ValidationError> validate(Object object) throws IllegalAccessException {
        fillFunctions();
        processObject("", object,
                /*annotations for class*/ List.of(Constrained.class),
                /*annotations for primitive values*/ List.of(AnyOf.class, InRange.class, Positive.class, Negative.class, NotBlank.class),
                /*annotations for collections*/ List.of(NotEmpty.class, Size.class),
                /*annotations for every object*/ List.of(NotNull.class));
        return errors;
    }

    private void processObject(String path, Object object,
                               final List<Class<? extends Annotation>> classAnnotations,
                               final List<Class<? extends Annotation>> primitiveAnnotations,
                               final List<Class<? extends Annotation>> collectionAnnotations,
                               final List<Class<? extends Annotation>> objectAnnotations) throws IllegalAccessException {

        for (var annotation : classAnnotations)
            if (!object.getClass().isAnnotationPresent(annotation))
                return;


        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            StringBuilder pathBuilder = new StringBuilder();

            pathBuilder.append(path);
            if (!pathBuilder.toString().isEmpty()) pathBuilder.append('.');
            pathBuilder.append(field.getName());

            for (var annotation : objectAnnotations) {
                if (hasAnnotation(field, annotation))
                    functions.get(annotation).
                            validate(getAnnotation(field, annotation), field, field.get(object), pathBuilder.toString()).
                            ifPresent(errors::add);
            }

            if (field.get(object) == null)
                continue;

            final Object element = field.get(object);

            if (List.class.isAssignableFrom(element.getClass())) {
                List<Object> list = (List<Object>) field.get(object);

                for (var annotation : collectionAnnotations) {
                    if (hasAnnotation(field, annotation))
                        functions.get(annotation).
                                validate(getAnnotation(field, annotation), field, list, pathBuilder.toString()).
                                ifPresent(errors::add);
                }

                for (int i = 0; i < list.size(); i++) {
                    String indexedPath = pathBuilder.toString() + "[" + i + "]";

                    for (var annotation : objectAnnotations) {
                        if (hasParameterizedAnnotation(field, annotation))
                            functions.get(annotation).
                                    validate(getParameterizedAnnotation(field, annotation), field, list.get(i), pathBuilder.toString()).
                                    ifPresent(errors::add);
                    }

                    for (var annotation : primitiveAnnotations) {
                        if (hasParameterizedAnnotation(field, annotation))
                            functions.get(annotation).
                                    validate(getParameterizedAnnotation(field, annotation), field, list.get(i), indexedPath).
                                    ifPresent(errors::add);
                    }

//                  List in list case
                    if (list.get(i) instanceof Collection<?>) {
                        final Collection<?> collection = (Collection<?>) element;

                        for (var annotation : collectionAnnotations) {
                            if (hasParameterizedAnnotation(field, annotation))
                                functions.get(annotation).
                                        validate(getParameterizedAnnotation(field, annotation), field, collection, indexedPath).
                                        ifPresent(errors::add);
                        }
                    }

                    processObject(indexedPath, list.get(i),
                            classAnnotations, primitiveAnnotations, collectionAnnotations, objectAnnotations);
                }
            } else {
                for (var annotation : objectAnnotations) {
                    if (hasAnnotation(field, annotation))
                        functions.get(annotation).
                                validate(getAnnotation(field, annotation), field, element, pathBuilder.toString()).
                                ifPresent(errors::add);
                }

                for (var annotation : primitiveAnnotations) {
                    if (hasAnnotation(field, annotation))
                        functions.get(annotation).
                                validate(getAnnotation(field, annotation), field, element, pathBuilder.toString()).
                                ifPresent(errors::add);
                }

                processObject(pathBuilder.toString(), field.get(object),
                        classAnnotations, primitiveAnnotations, collectionAnnotations, objectAnnotations);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ValidationError error : errors) {
            stringBuilder.append(error.getPath()).append(" | ").append(error.getMessage()).append(" | ").append(error.getFailedValue()).append('\n');
        }
        return stringBuilder.toString();
    }
}
