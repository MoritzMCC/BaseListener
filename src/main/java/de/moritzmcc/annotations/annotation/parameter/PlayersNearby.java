package de.moritzmcc.annotations.annotation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlayersNearby {
    double radius() default 10.0;
    int minPlayers() default 0;
}
