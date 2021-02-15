package com.ddmukhin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface InRange {

    public long min() default Long.MIN_VALUE;

    public long max() default Long.MAX_VALUE;

}
