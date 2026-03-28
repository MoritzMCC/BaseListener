package de.MoritzMCC.anntotations;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationRegestry {
    private static final Map<Class<? extends Annotation>, AnnotationHandler<?>> handlers = new ConcurrentHashMap<>();

    private AnnotationRegestry() {}

    public static <A extends Annotation> AnnotationHandler<A> getHandler(Class<A> annotationType) {
        return (AnnotationHandler<A>) handlers.get(annotationType);
    }

    public static void registerHandler(Class<? extends Annotation> annotationType, AnnotationHandler<?> handler) {
        handlers.put(annotationType, handler);
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private final Map<Class<? extends Annotation>, AnnotationHandler<?>> map = new HashMap<>();

        public <A extends Annotation> Builder register(Class<A> annotationType, AnnotationHandler<A> handler) {
            map.put(annotationType, handler);
            return this;
        }
        public void build() {
            handlers.putAll(map);
        }
    }
}
