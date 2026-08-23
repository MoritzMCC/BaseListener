package de.moritzmcc.annotations.impl;

import de.moritzmcc.annotations.AnnotationHandler;
import de.moritzmcc.annotations.Result;
import de.moritzmcc.annotations.annotation.Cooldown;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownHandler implements AnnotationHandler<Cooldown> {

    private final Map<UUID, Map<Method, Long>> playerCooldowns = new ConcurrentHashMap<>();
    private final Map<Method, Long> globalCooldowns = new ConcurrentHashMap<>();

    @Override
    public Result handle(Cooldown annotation, Event event, Method method) {
        long durationMillis = annotation.seconds() * 1000L + annotation.milliseconds();
        long now = System.currentTimeMillis();

        return switch (annotation.scope()) {
            case GLOBAL -> checkAndUpdate(globalCooldowns, method, now, durationMillis);
            case PLAYER -> {
                if (!(event instanceof PlayerEvent playerEvent)) yield Result.SKIP;
                UUID uuid = playerEvent.getPlayer().getUniqueId();
                Map<Method, Long> perPlayer = playerCooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
                yield checkAndUpdate(perPlayer, method, now, durationMillis);
            }
        };
    }

    private Result checkAndUpdate(Map<Method, Long> store, Method method, long now, long durationMillis) {
        Long next = store.get(method);
        if (next != null && now < next) {
            return Result.CANCEL;
        }
        store.put(method, now + durationMillis);
        return Result.CONTINUE;
    }
}
