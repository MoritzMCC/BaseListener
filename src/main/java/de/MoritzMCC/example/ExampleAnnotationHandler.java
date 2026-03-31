package de.MoritzMCC.example;

import de.MoritzMCC.anntotations.AnnotationHandler;
import de.MoritzMCC.anntotations.Result;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * The type Example annotation handler.
 */
public class ExampleAnnotationHandler implements AnnotationHandler<ExampleAnnotation> {

    /**
     *
     * @param annotation The annotation which should be handled
     * @param event The parameter of the methode
     * @param method the Methode of the annotation
     * @return if the methode should be executed
     *
     * cancelEvent() returns false and cancels the event if possible
     */
    @Override
    public Result handle(ExampleAnnotation annotation, Event event, Method method) {

        if(event instanceof Cancellable)return Result.CANCEL;
        Main.getInstance().getLogger().info(annotation.value());

        /**
         * if (User.getUser(event.getPlayer()).hasRank(XY))return true;
         * return false;
         */
        return Result.CONTINUE;
    }
}
