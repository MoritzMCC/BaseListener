package de.moritzmcc.annotations.impl;

import de.moritzmcc.annotations.AnnotationHandler;
import de.moritzmcc.annotations.Result;
import de.moritzmcc.annotations.annotation.Limit;
import de.moritzmcc.baseListener.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LimitHandler implements AnnotationHandler<Limit> {

    private final Map<UUID, Map<Method, AtomicInteger>> playerLimits = new ConcurrentHashMap<>();
    private final Map<Method, AtomicInteger> globalLimits = new ConcurrentHashMap<>();

    @Override
    public Result handle(Limit annotation, Event event, Method method) {
        return switch (annotation.scope()) {
            case GLOBAL -> tryConsume(globalLimits, method, annotation);
            case PLAYER -> {
                if (!(event instanceof PlayerEvent playerEvent)) yield Result.SKIP;
                UUID uuid = playerEvent.getPlayer().getUniqueId();
                Map<Method, AtomicInteger> perPlayer = playerLimits.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
                yield tryConsume(perPlayer, method, annotation);
            }
        };
    }

    private Result tryConsume(Map<Method, AtomicInteger> store, Method method, Limit annotation) {
        AtomicInteger counter = store.computeIfAbsent(method, k -> new AtomicInteger());
        int limit = annotation.limit();

        while (true) {
            int current = counter.get();
            if (current >= limit) {
                return Result.CANCEL;
            }
            if (counter.compareAndSet(current, current + 1)) {
                scheduleDecrement(counter, annotation.resetAfter());
                return Result.CONTINUE;
            }
        }
    }

    private void scheduleDecrement(AtomicInteger counter, int resetAfterSeconds) {
        Bukkit.getScheduler().runTaskLater(
                EventManager.getInstance().getPlugin(),
                () -> counter.updateAndGet(v -> Math.max(0, v - 1)),
                resetAfterSeconds * 20L
        );
    }
}
