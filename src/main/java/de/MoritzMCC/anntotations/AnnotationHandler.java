package de.MoritzMCC.anntotations;

import org.bukkit.event.Event;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface AnnotationHandler<A extends Annotation> {

    boolean handle(A annotation, Event event);
}
