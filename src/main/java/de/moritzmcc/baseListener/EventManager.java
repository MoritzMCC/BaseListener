package de.moritzmcc.baseListener;

import de.moritzmcc.annotations.AnnotationManager;
import de.moritzmcc.parameterresolver.ParameterManager;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventManager {

    private static volatile EventManager instance;

    private final Set<BaseListener> listeners = new CopyOnWriteArraySet<>();

    private final Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        instance = this;

        new EventDispatchRegistry(plugin);
        new AnnotationManager(plugin).register();
        ParameterManager.register();
    }

    public void addListener(BaseListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BaseListener listener) {
        listeners.remove(listener);
    }

    public void dispatchEvent(Event event) {
        for (BaseListener listener : listeners) {
            listener.dispatch(event);
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public static EventManager getInstance() {
        return instance;
    }
}
