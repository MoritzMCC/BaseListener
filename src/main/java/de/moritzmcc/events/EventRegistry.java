package de.moritzmcc.events;

import org.bukkit.event.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventRegistry {

    private static final Set<Class<? extends Event>> customEvents = ConcurrentHashMap.newKeySet();

    private EventRegistry() {}

    public static void registerCustomEvent(Class<? extends Event> clazz) {
        customEvents.add(clazz);
    }

    public static Set<Class<? extends Event>> getCustomEvents() {
        return new HashSet<>(customEvents);
    }
}
