package de.MoritzMCC.baseListener;

import de.MoritzMCC.anntotations.AnnotationHandler;
import de.MoritzMCC.anntotations.AnnotationRegestry;
import org.bukkit.event.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public abstract class BaseListener {

    private static final Map<Class<? extends Annotation>, AnnotationHandler<?>> globalAnnotationHandlers = new HashMap<>();

    public static <A extends Annotation> void registerAnnotationHandler(Class<A> annotationClass, AnnotationHandler<A> handler) {
        globalAnnotationHandlers.put(annotationClass, handler);
    }

    private final Map<Class<? extends Event>, List<Method>> handlers = new HashMap<>();

    protected BaseListener() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.getParameterCount() != 1) continue;
            if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) continue;

            Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];

            handlers.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(method);
        }

        EventManager.getInstance().addListener(this);
    }

    public void dispatch(Event event) {
        Class<?> clazz = event.getClass();

        while (clazz != null && Event.class.isAssignableFrom(clazz)) {
            List<Method> methods = handlers.get(clazz);
            if (methods != null) {
                for (Method method : methods) {
                    try {
                        boolean proceed = true;
                        for (Annotation ann : method.getAnnotations()) {
                            AnnotationHandler handler = AnnotationRegestry.getHandler(ann.annotationType());
                            if (handler != null) {
                                proceed = handler.handle(ann, event);
                                if (!proceed) break;
                            }
                        }
                        if (proceed) method.invoke(this, event);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}