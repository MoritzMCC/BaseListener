package de.MoritzMCC.anntotations;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@FunctionalInterface
public interface AnnotationHandler<A extends Annotation> {

    Result handle(A annotation, Event event, Method method);
}
