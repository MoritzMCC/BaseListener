package de.MoritzMCC.baseListener;

import de.MoritzMCC.anntotations.AnnotationManger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class EventManager {

    @Getter
    private static EventManager instance;
    private final Set<BaseListener> listeners;
    @Getter
    private Plugin plugin;

    public EventManager(Plugin plugin) {
        listeners = new HashSet<>();
        this.plugin = plugin;
        instance = this;
        registerAnnotations();
        Bukkit.getPluginManager().registerEvents( new MainListener(plugin), plugin);
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

    private void registerAnnotations(){
        new AnnotationManger(plugin).register();
    }

}
