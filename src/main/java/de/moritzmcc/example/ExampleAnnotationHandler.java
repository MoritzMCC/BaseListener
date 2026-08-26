package de.moritzmcc.example;

import de.moritzmcc.annotations.AnnotationHandler;
import de.moritzmcc.annotations.Result;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.lang.reflect.Method;

/**
 * Example handler for a custom annotation.
 * <p>The event is always logged first; if it happens to be cancellable, it is
 * cancelled afterward.</p>
 */
public class ExampleAnnotationHandler implements AnnotationHandler<ExampleAnnotation> {

    /**
     * @param annotation the annotation being processed
     * @param event      the event of the method
     * @param method     the annotated method
     * @return whether the method should be executed
     */
    @Override
    public Result handle(ExampleAnnotation annotation, Event event, Method method) {
        Main.getInstance().getLogger().info(annotation.value());

        if (event instanceof Cancellable) {
            return Result.CANCEL;
        }
        return Result.CONTINUE;
    }
}
