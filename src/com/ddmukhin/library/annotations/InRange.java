package com.ddmukhin.library.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface InRange {

    long min() default Long.MIN_VALUE;

   long max() default Long.MAX_VALUE;

}
