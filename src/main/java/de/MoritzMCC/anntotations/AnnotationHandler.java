package de.MoritzMCC.anntotations;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@FunctionalInterface
public interface AnnotationHandler<A extends Annotation> {

    boolean handle(A annotation, Event event, Method method);

    default boolean cancelEvent(Event event) {
        if (event instanceof Cancellable cancellable) cancellable.setCancelled(true);
        return false;
    }
}
