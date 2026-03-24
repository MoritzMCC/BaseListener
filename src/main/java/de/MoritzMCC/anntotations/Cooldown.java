package de.MoritzMCC.anntotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cooldown {
    int seconds() default 0;
    int milliseconds() default 0;
    Scope scope() default Scope.PLAYER;

}
