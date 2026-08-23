package de.moritzmcc.example;

import de.moritzmcc.annotations.AnnotationHandler;
import de.moritzmcc.annotations.Result;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.lang.reflect.Method;

/**
 * Beispiel-Handler fuer eine eigene Annotation.
 *
 * FIX gegenueber der Ausgangsversion: dort wurde bei einem Cancellable-Event sofort
 * Result.CANCEL zurueckgegeben, BEVOR die Log-Zeile ausgefuehrt wurde - das Beispiel
 * suggerierte damit faelschlich, @ExampleAnnotation wuerde nie loggen, wenn das Event
 * cancelbar ist. Jetzt: erst loggen, dann ggf. canceln.
 */
public class ExampleAnnotationHandler implements AnnotationHandler<ExampleAnnotation> {

    /**
     * @param annotation die zu verarbeitende Annotation
     * @param event      das Event der Methode
     * @param method     die annotierte Methode
     * @return ob die Methode ausgefuehrt werden soll
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
