package com.ddmukhin.debug;

import com.ddmukhin.annotations.Constrained;
import com.ddmukhin.annotations.NotNull;
import com.ddmukhin.annotations.Size;
import com.ddmukhin.validation.errors.SimpleValidationError;
import com.ddmukhin.validation.errors.ValidationError;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IllegalAccessException {
        GuestForm form = new GuestForm("abc", "def", 0);

        List<GuestForm> list = new ArrayList<>();
        list.add(form);
        list.add(new GuestForm("abc2", "def2", 1));

        BookingForm bForm = new BookingForm(list, List.of("TV", "Piano"), "prop");

        processObject("", form, -1);

        for(var error : errors){
            System.out.println(error.getMessage() + " " + error.getPath() + " " + error.getFailedValue());
        }
    }

    static Collection<ValidationError> errors = new HashSet<>();

    private static void processObject(String path, Object object, int index) throws IllegalAccessException {
        if(!path.isEmpty()) {
//          Entry point for primitive values check
//          path = path
        }

        if(isPrimitive(object.getClass())) {
            if(index != -1){
//              Entry point for primitive values check (in list)
//              path = path + "[" + index + "]
            }
            return;
        }

        for(Field field : object.getClass().getDeclaredFields()){
            field.setAccessible(true);
            StringBuilder pathBuilder = new StringBuilder(path);
            if(index != -1) pathBuilder.append("[").append(index).append("]");
            if(!path.isEmpty()) pathBuilder.append(".");
            pathBuilder.append(field.getName());

            if(field.get(object) == null) {
                //Entry point for null check (NotNull)
//                path = pathBuilder.toString()
                continue;
            }

            if(Collection.class.isAssignableFrom(field.getType())){
                List<Object> list = (List<Object>) field.get(object);
//                Entry point for list check (InRange)
                for(int i = 0; i < list.size(); i++){
//                    if ...
                    processObject(pathBuilder.toString(), list.get(i), i);
                }
            }else{
                processObject(pathBuilder.toString(), field.get(object), -1);
            }
        }
    }

//    private static Set<? extends Annotation> getAnnotations(Object object){
//
//    }

    private static final Set<Class<?>> WRAPPER_TYPES = new HashSet(Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    public static boolean isPrimitive(Class<?> clazz){
        return clazz.isPrimitive() || isWrapperType(clazz) || clazz.equals(String.class);
    }
}
