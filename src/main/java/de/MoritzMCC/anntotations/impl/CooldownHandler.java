package de.MoritzMCC.anntotations.impl;

import de.MoritzMCC.anntotations.Result;
import de.MoritzMCC.example.Main;
import de.MoritzMCC.anntotations.AnnotationHandler;
import de.MoritzMCC.anntotations.annotation.Cooldown;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownHandler implements AnnotationHandler<Cooldown> {

 private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();
 private final Map<String, Long> globalCooldowns = new ConcurrentHashMap<>();

    @Override
    public Result handle(Cooldown annotation, Event event, Method method) {
        Main.getInstance().getLogger().info("Cooldown handler called");
        switch (annotation.scope()){
            case GLOBAL:
                long now = System.currentTimeMillis();
                long next = globalCooldowns.getOrDefault(method.toGenericString(), 0L);
                if (next < now) return Result.CANCEL;
                globalCooldowns.put(method.toGenericString(), now + annotation.seconds() * 1000L + annotation.milliseconds());
                return Result.CONTINUE;
            case PLAYER:
            default:
                if (!(event instanceof PlayerEvent playerEvent))return Result.SKIP;
                UUID uuid = playerEvent.getPlayer().getUniqueId();
                cooldowns.putIfAbsent(uuid,new ConcurrentHashMap<>());
                long now_ = System.currentTimeMillis();
                long next_ = cooldowns.get(uuid).getOrDefault(method.toGenericString(), 0L);
                if (now_ < next_)return Result.CANCEL;
                cooldowns.get(uuid).put(method.toGenericString(), now_ + annotation.seconds() * 1000L + annotation.milliseconds() );
                return Result.CONTINUE;
        }
    }
}
