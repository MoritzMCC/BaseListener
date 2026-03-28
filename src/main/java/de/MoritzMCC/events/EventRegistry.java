package de.MoritzMCC.events;

import de.MoritzMCC.baseListener.EventManager;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;

import java.util.HashSet;
import java.util.Set;

public class EventRegistry {

    @Getter
    private static final Set<Class<? extends Event>> customEvents = new HashSet<>();

    public static void registerCustomEvent(Class<? extends Event> clazz) {
        customEvents.add(clazz);
    }
}
