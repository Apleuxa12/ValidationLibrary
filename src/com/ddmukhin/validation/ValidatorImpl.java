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
    public Set<ValidationError> validate(Object object) {

        return errors;
    }

    private void processObject(String path, Object object) throws IllegalAccessException {
        if(!object.getClass().isAnnotationPresent(Constrained.class)){
            return;
        }

        if(ObjectUtils.isPrimitive(object.getClass())) {
            return;
        }

        for(Field field : object.getClass().getDeclaredFields()){
            field.setAccessible(true);
            StringBuilder pathBuilder = new StringBuilder(path);
            pathBuilder.append(path);
            if(!pathBuilder.toString().isEmpty()) pathBuilder.append(".");
            pathBuilder.append(field.getName());

            if(field.get(object) == null) {
                if(field.isAnnotationPresent(NotNull.class)){
                    errors.add(new NotNullError(pathBuilder.toString()));
                }
                continue;
            }

            if(Collection.class.isAssignableFrom(field.getType())){
                List<Object> list = (List<Object>) field.get(object);
                if(field.isAnnotationPresent(NotEmpty.class)){
                    if(list.isEmpty())
                        errors.add(new NotEmptyError(pathBuilder.toString()));
                }
                if(field.isAnnotationPresent(Size.class)){
                    var annotation = field.getAnnotation(Size.class);
                    if(list.size() < annotation.min() || list.size() > annotation.max()){
                        errors.add(new SizeError(annotation.min(), annotation.max(), pathBuilder.toString(), list.size()));
                    }
                }
                AnnotatedParameterizedType listType =
                        (AnnotatedParameterizedType) field.getAnnotatedType();
                AnnotatedType annType = listType.getAnnotatedActualTypeArguments()[0];
                for(int i = 0; i < list.size(); i++){
                    final Object element = list.get(i);
                    pathBuilder.append("[").append(i).append("]");

                    for(Annotation annotation : annType.getAnnotations()){
                        if(annotation.annotationType().equals(Positive.class) && element instanceof Number){
                            if(((Number) element).intValue() <= 0){
                                errors.add(new PositiveError(pathBuilder.toString(), element));
                            }
                        }
                        if(annotation.annotationType().equals(Negative.class) && element instanceof Number){
                            if(((Number) element).intValue() >= 0){
                                errors.add(new NegativeError(pathBuilder.toString(), element));
                            }
                        }
                        if(annotation.annotationType().equals(NotBlank.class) && element.getClass().equals(String.class)){
                            if(((String) element).isBlank()){
                                errors.add(new BlankError(pathBuilder.toString()));
                            }
                        }
                        if(annotation.annotationType().equals(InRange.class) && element instanceof Comparable){
                            if(((Number) element).intValue() < ((InRange) annotation).min()
                                    || ((Number) element).intValue() > ((InRange) annotation).max()){
                                errors.add(new InRangeError(((InRange) annotation).min(), ((InRange) annotation).max(),
                                        pathBuilder.toString(), element));
                            }
                        }
                        if(annotation.annotationType().equals(AnyOf.class) && element.getClass().equals(String.class)){
                            if(!Arrays.asList(((AnyOf) annotation).value()).contains((element))){
                                errors.add(new AnyOfError(Arrays.asList(((AnyOf) annotation).value()), pathBuilder.toString(), element));
                            }
                        }
                    }

                    processObject(pathBuilder.toString(), list.get(i));
                }
            }else{
                final Object element = field.get(object);
                if(field.isAnnotationPresent(Positive.class) && element instanceof Number){
                    if(((Number) element).intValue() <= 0){
                        errors.add(new PositiveError(pathBuilder.toString(), element));
                    }
                }
                if(field.isAnnotationPresent(Negative.class) && element instanceof Number){
                    if(((Number) element).intValue() >= 0){
                        errors.add(new NegativeError(pathBuilder.toString(), element));
                    }
                }
                if(field.isAnnotationPresent(NotBlank.class) && element.getClass().equals(String.class)){
                    if(((String) element).isBlank()){
                        errors.add(new BlankError(pathBuilder.toString()));
                    }
                }
                if(field.isAnnotationPresent(InRange.class) && element instanceof Comparable){
                    var annotation = field.getAnnotation(InRange.class);
                    if(((Number) element).intValue() < (annotation).min()
                            || ((Number) element).intValue() > (annotation).max()){
                        errors.add(new InRangeError((annotation).min(), (annotation).max(),
                                pathBuilder.toString(), element));
                    }
                }
                if(field.isAnnotationPresent(AnyOf.class) && element.getClass().equals(String.class)){
                    var annotation = field.getAnnotation(AnyOf.class);
                    if(!Arrays.asList((annotation).value()).contains((element))){
                        errors.add(new AnyOfError(Arrays.asList((annotation).value()), pathBuilder.toString(), element));
                    }
                }
                processObject(pathBuilder.toString(), field.get(object));
            }
        }
    }
}
