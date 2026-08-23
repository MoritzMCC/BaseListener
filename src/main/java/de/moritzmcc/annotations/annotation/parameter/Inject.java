package de.moritzmcc.annotations.annotation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rein dokumentierende Markierung: kennzeichnet einen Parameter als "wird injiziert".
 * Die tatsaechliche Aufloesung erfolgt ausschliesslich ueber {@link de.moritzmcc.parameterresolver.ParameterResolver#canResolve},
 * diese Annotation selbst hat keinen Einfluss auf die Aufloesungslogik.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Inject {
}
