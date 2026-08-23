package de.moritzmcc.annotations.annotation;

import de.moritzmcc.annotations.CancelCondition;
import org.bukkit.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CancelIf {
    Class<? extends CancelCondition<?>> condition() default DefaultCancelCondition.class;
    boolean cancel() default false;
}

class DefaultCancelCondition implements CancelCondition<Event> {
    @Override
    public boolean check(Event event) {
        return false;
    }
}
