package com.ddmukhin.library.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ObjectUtils {

    private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<>(Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    public static boolean isPrimitive(Class<?> clazz){
        return clazz.isPrimitive() || isWrapperType(clazz) || clazz.equals(String.class);
    }

    public static boolean isBaseJavaClass(Class<?> clazz){
        return clazz.getName().split("\\.")[0].equals("java");
    }
}
