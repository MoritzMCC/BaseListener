package de.moritzmcc.annotations.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {
    boolean playerName() default true;
    boolean eventName() default true;
    boolean location() default false;
    boolean blockType() default false;
    boolean id() default true;
    String message() default "";
}
