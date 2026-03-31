package de.MoritzMCC.parameterresolver;

import de.MoritzMCC.baseListener.BaseListener;
import org.bukkit.event.Event;

public interface ParameterResolver {
    boolean canResolve(Class<?> type);
    Object resolve(Event event, BaseListener listener);
}
