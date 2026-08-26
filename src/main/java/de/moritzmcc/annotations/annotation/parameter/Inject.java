package de.moritzmcc.annotations.annotation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Purely documentational marker that flags a parameter as "will be injected".
 * Actual resolution happens exclusively via {@link de.moritzmcc.parameterresolver.ParameterResolver#canResolve};
 * this annotation itself has no effect on the resolution logic.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Inject {
}
