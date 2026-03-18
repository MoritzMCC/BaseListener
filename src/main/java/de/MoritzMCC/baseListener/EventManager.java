package de.MoritzMCC.baseListener;

import de.MoritzMCC.anntotations.AnnotationRegestry;
import de.MoritzMCC.anntotations.CancelCondition;
import de.MoritzMCC.anntotations.cancelIf;
import de.MoritzMCC.anntotations.isPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventManager {

    @Getter
    private static EventManager instance;
    private final Set<BaseListener> listeners;

    public EventManager(Plugin plugin) {
        listeners = new HashSet<>();
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
        AnnotationRegestry.builder()
                .register(cancelIf.class, ((annotation, event) -> {
                    if (!(event instanceof Cancellable cancelEvent))return true;
                    try {
                        CancelCondition<Event> condition = (CancelCondition<Event>) ((cancelIf) annotation).condition().getDeclaredConstructor().newInstance();
                        if (condition.check(event)){
                            cancelEvent.setCancelled(true);
                            return false;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }return true;

                }))
                .register(isPlayer.class, ((annotation, event) ->{
                    return event instanceof PlayerEvent ;
                })).build();
    }
}
