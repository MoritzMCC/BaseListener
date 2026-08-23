package de.moritzmcc.parameterresolver;

import de.moritzmcc.baseListener.BaseListener;
import org.bukkit.event.Event;

import java.lang.reflect.Parameter;

public interface ParameterResolver {
    boolean canResolve(Parameter parameter);
    Object resolve(Parameter parameter, Event event, BaseListener listener);
}
