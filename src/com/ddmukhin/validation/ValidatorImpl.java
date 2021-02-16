package com.ddmukhin.validation;

import com.ddmukhin.annotations.Constrained;
import com.ddmukhin.validation.errors.ValidationError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class ValidatorImpl implements Validator{
    @Override
    public Set<ValidationError> validate(Object object) {
        Set<ValidationError> errors = new HashSet<>();



        return errors;
    }

    List<String> paths = new ArrayList<>();

    private void processObject(String path, Object object, int index) throws IllegalAccessException {
        if(!path.isEmpty())
            paths.add(path);
        for(Field field : object.getClass().getFields()){
            String localPath = path.isEmpty() ? field.getName() :
                    index == -1 ? path + "." + field.getName() : path + "[" + index + "]." + field.getName();
            if(Collection.class.isAssignableFrom(field.getType())){
                List<?> list = (List<?>) field.get(object);
                for(int i = 0; i < list.size(); i++){
                    processObject(localPath, list.get(i), i);
                }
            }else{
                processObject(localPath, field, -1);
            }
        }
    }
}
