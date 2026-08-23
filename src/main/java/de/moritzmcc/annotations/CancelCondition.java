package de.moritzmcc.annotations;

import org.bukkit.event.Event;

@FunctionalInterface
public interface CancelCondition<T extends Event> {
    boolean check(T event);
}
