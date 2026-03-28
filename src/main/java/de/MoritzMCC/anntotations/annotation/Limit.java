package de.MoritzMCC.anntotations.annotation;

import de.MoritzMCC.anntotations.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Limit {
    int limit() default 1;
    int resetAfter() default 1; //seconds
    Scope scope() default Scope.PLAYER;
}
