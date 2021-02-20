package com.ddmukhin.library.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AnnotationUtils {

    public static boolean hasAnnotation(Field field, final Class<? extends Annotation> annotation) {
        AnnotatedType type = field.getAnnotatedType();
        return Arrays.stream(type.getAnnotations()).map(Annotation::annotationType).anyMatch(x -> x.equals(annotation));
    }

    public static <T extends Annotation> T getAnnotation(Field field, Class<T> annotationType) {
        AnnotatedType type = field.getAnnotatedType();
        return type.getAnnotationsByType(annotationType)[0];
    }

    public static boolean hasParameterizedAnnotation(Field field, final Class<? extends Annotation> annotation) {
        AnnotatedParameterizedType listType = (AnnotatedParameterizedType) field.getAnnotatedType();
        return Arrays.stream(listType.getAnnotatedActualTypeArguments()[0].getAnnotations()).
                map(Annotation::annotationType).anyMatch(x -> x.equals(annotation));
    }

    public static <T extends Annotation> T getParameterizedAnnotation(Field field, Class<T> annotationType) {
        AnnotatedParameterizedType listType = (AnnotatedParameterizedType) field.getAnnotatedType();
        return listType.getAnnotatedActualTypeArguments()[0].getAnnotationsByType(annotationType)[0];
    }
}
