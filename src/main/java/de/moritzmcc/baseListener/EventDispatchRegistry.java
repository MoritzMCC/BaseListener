package de.moritzmcc.baseListener;

import de.moritzmcc.events.EventRegistry;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class EventDispatchRegistry implements Listener {

    @Getter
    private static volatile EventDispatchRegistry instance;

    private final Plugin plugin;
    private final Set<Class<? extends Event>> registered = ConcurrentHashMap.newKeySet();
    private volatile Reflections reflections;

    EventDispatchRegistry(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public void ensureRegistered(Class<? extends Event> eventType) {
        if (Modifier.isAbstract(eventType.getModifiers())) {
            for (Class<? extends Event> concrete : resolveConcreteSubtypes(eventType)) {
                registerExact(concrete);
            }
        } else {
            registerExact(eventType);
        }
    }

    private void registerExact(Class<? extends Event> eventClass) {
        if (!registered.add(eventClass)) {
            return;
        }

        try {
            eventClass.getMethod("getHandlerList");
        } catch (NoSuchMethodException e) {
            plugin.getLogger().warning("Event " + eventClass.getSimpleName()
                    + " no getHandlerList() found.");
            return;
        }

        Bukkit.getPluginManager().registerEvent(
                eventClass,
                this,
                EventPriority.NORMAL,
                (listener, event) -> EventManager.getInstance().dispatchEvent(event),
                plugin
        );


        plugin.getLogger().fine(() -> "event registered: " + eventClass.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<? extends Event>> resolveConcreteSubtypes(Class<? extends Event> abstractType) {
        if (reflections == null) {
            synchronized (this) {
                if (reflections == null) {
                    reflections = new Reflections("org.bukkit.event");
                }
            }
        }

        Set<Class<? extends Event>> concrete = new HashSet<>();

        for (Class<?> sub : reflections.getSubTypesOf(abstractType)) {
            if (!Modifier.isAbstract(sub.getModifiers()) && Event.class.isAssignableFrom(sub)) {
                concrete.add((Class<? extends Event>) sub);
            }
        }

        for (Class<? extends Event> custom : EventRegistry.getCustomEvents()) {
            if (abstractType.isAssignableFrom(custom)) {
                concrete.add(custom);
            }
        }

        return concrete;
    }
}
