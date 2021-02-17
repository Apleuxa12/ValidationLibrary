package com.ddmukhin.validation;

import com.ddmukhin.annotations.*;
import com.ddmukhin.utils.ObjectUtils;
import com.ddmukhin.validation.errors.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.*;

public class ValidatorImpl implements Validator{

    private final Set<ValidationError> errors = new HashSet<>();

    @Override
    public Set<ValidationError> validate(Object object) throws IllegalAccessException {
        processObject("", object);
        return errors;
    }

    private void processObject(String path, Object object) throws IllegalAccessException {
        if(ObjectUtils.isPrimitive(object.getClass()) || !object.getClass().isAnnotationPresent(Constrained.class)) {
            return;
        }

        for(Field field : object.getClass().getDeclaredFields()){
            field.setAccessible(true);

            StringBuilder pathBuilder = new StringBuilder();

            pathBuilder.append(path);
            if(!pathBuilder.toString().isEmpty()) path += '.';
            pathBuilder.append(field.getName());
            if(field.get(object) == null) {
                if(hasAnnotation(field, NotNull.class)){
                    errors.add(new NotNullError(pathBuilder.toString()));
                }
                continue;
            }

            if(Collection.class.isAssignableFrom(field.getType())){
                List<Object> list = (List<Object>) field.get(object);
                if(hasAnnotation(field, NotEmpty.class)){
                    if(list.isEmpty())
                        errors.add(new NotEmptyError(pathBuilder.toString()));
                }
                if(hasAnnotation(field, Size.class)){
                    var annotation = this. <Size> getAnnotation(field, Size.class);
                    if(list.size() < annotation.min() || list.size() > annotation.max()){
                        errors.add(new SizeError(annotation.min(), annotation.max(), pathBuilder.toString(), list.size()));
                    }
                }

                for(int i = 0; i < list.size(); i++){
                    final Object element = list.get(i);
                    for(Annotation annotation : this.getParameterizedAnnotations(field)){
                        if(annotation.annotationType().equals(Positive.class) && element instanceof Number){
                            if(((Number) element).intValue() <= 0){
                                errors.add(new PositiveError(pathBuilder.toString() + "[" + i + "]", element));
                            }
                        }
                        if(annotation.annotationType().equals(Negative.class) && element instanceof Number){
                            if(((Number) element).intValue() >= 0){
                                errors.add(new NegativeError(pathBuilder.toString() + "[" + i + "]", element));
                            }
                        }
                        if(annotation.annotationType().equals(NotBlank.class) && element.getClass().equals(String.class)){
                            if(((String) element).isBlank()){
                                errors.add(new BlankError(pathBuilder.toString() + "[" + i + "]"));
                            }
                        }
                        if(annotation.annotationType().equals(InRange.class) && element instanceof Comparable){
                            if(((Number) element).intValue() < ((InRange) annotation).min()
                                    || ((Number) element).intValue() > ((InRange) annotation).max()){
                                errors.add(new InRangeError(((InRange) annotation).min(), ((InRange) annotation).max(),
                                        pathBuilder.toString() + "[" + i + "]", element));
                            }
                        }
                        if(annotation.annotationType().equals(AnyOf.class) && element.getClass().equals(String.class)){
                            if(!Arrays.asList(((AnyOf) annotation).value()).contains((element))){
                                errors.add(new AnyOfError(Arrays.asList(((AnyOf) annotation).value()), pathBuilder.toString() + "[" + i + "]", element));
                            }
                        }
                    }

                    processObject(pathBuilder.toString() + "[" + i + "]", list.get(i));
                }
            }else{
                final Object element = field.get(object);
                if(hasAnnotation(field, Positive.class) && element instanceof Number){
                    if(((Number) element).intValue() <= 0){
                        errors.add(new PositiveError(pathBuilder.toString(), element));
                    }
                }
                if(hasAnnotation(field, Negative.class) && element instanceof Number){
                    if(((Number) element).intValue() >= 0){
                        errors.add(new NegativeError(pathBuilder.toString(), element));
                    }
                }
                if(hasAnnotation(field, NotBlank.class) && element.getClass().equals(String.class)){
                    if(((String) element).isBlank()){
                        errors.add(new BlankError(pathBuilder.toString()));
                    }
                }
                if(hasAnnotation(field, InRange.class) && element instanceof Comparable){
                    var annotation = this. <InRange> getAnnotation(field, InRange.class);
                    if(((Number) element).intValue() < (annotation).min()
                            || ((Number) element).intValue() > (annotation).max()){
                        errors.add(new InRangeError((annotation).min(), (annotation).max(),
                                pathBuilder.toString(), element));
                    }
                }
                if(hasAnnotation(field, AnyOf.class) && element.getClass().equals(String.class)){
                    var annotation = this. <AnyOf> getAnnotation(field, AnyOf.class);
                    if(!Arrays.asList((annotation).value()).contains((element))){
                        errors.add(new AnyOfError(Arrays.asList((annotation).value()), pathBuilder.toString(), element));
                    }
                }
                processObject(pathBuilder.toString(), field.get(object));
            }
        }
    }

    private List<Annotation> getParameterizedAnnotations(Field field){
        AnnotatedParameterizedType listType =
                (AnnotatedParameterizedType) field.getAnnotatedType();
        AnnotatedType annType = listType.getAnnotatedActualTypeArguments()[0];
        return Arrays.asList(annType.getAnnotations());
    }

    private boolean hasAnnotation(Field field, Class<? extends Annotation> annotation){
        AnnotatedType type = field.getAnnotatedType();
        return Arrays.stream(type.getAnnotations()).map(Annotation::annotationType).anyMatch(x -> x.equals(annotation));
    }

    private <T extends Annotation> T getAnnotation(Field field, Class<? extends Annotation> annotationType){
        AnnotatedType type = field.getAnnotatedType();
        return (T) type.getAnnotationsByType(annotationType)[0];
    }
}
