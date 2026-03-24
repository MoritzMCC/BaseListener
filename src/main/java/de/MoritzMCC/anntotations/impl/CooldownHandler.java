package de.MoritzMCC.anntotations.impl;

import de.MoritzMCC.Main;
import de.MoritzMCC.anntotations.AnnotationHandler;
import de.MoritzMCC.anntotations.Cooldown;
import de.MoritzMCC.anntotations.Scope;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownHandler implements AnnotationHandler<Cooldown> {

 private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();
 private final Map<String, Long> globalCooldowns = new ConcurrentHashMap<>();

    @Override
    public boolean handle(Cooldown annotation, Event event, Method method) {
        Main.getInstance().getLogger().info("Cooldown handler called");
        switch (annotation.scope()){
            case GLOBAL:
                long now = System.currentTimeMillis();
                long next = globalCooldowns.getOrDefault(method.toGenericString(), 0L);
                if (next < now) return cancelEvent(event);
                globalCooldowns.put(method.toGenericString(), now + annotation.seconds() * 1000L + annotation.milliseconds());
                return true;
            case PLAYER:
            default:
                if (!(event instanceof PlayerEvent playerEvent))return true;
                UUID uuid = playerEvent.getPlayer().getUniqueId();
                cooldowns.putIfAbsent(uuid,new ConcurrentHashMap<>());
                long now_ = System.currentTimeMillis();
                long next_ = cooldowns.get(uuid).getOrDefault(method.toGenericString(), 0L);
                if (now_ < next_)return cancelEvent(event);
                cooldowns.get(uuid).put(method.toGenericString(), now_ + annotation.seconds() * 1000L + annotation.milliseconds() );
                return true;
        }
    }


}
