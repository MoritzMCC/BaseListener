package de.moritzmcc.annotations.impl;

import de.moritzmcc.annotations.AnnotationHandler;
import de.moritzmcc.annotations.Result;
import de.moritzmcc.annotations.annotation.Throttle;
import org.bukkit.event.Event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ThrottleHandler implements AnnotationHandler<Throttle> {

    private static final class Window {
        final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
        final AtomicLong callCount = new AtomicLong();
    }

    private final Map<Method, Window> windows = new ConcurrentHashMap<>();

    @Override
    public Result handle(Throttle annotation, Event event, Method method) {
        Window window = windows.computeIfAbsent(method, k -> new Window());
        long now = System.currentTimeMillis();
        long windowMillis = annotation.perSeconds() * 1000L;

        long start = window.windowStart.get();
        if (now - start > windowMillis && window.windowStart.compareAndSet(start, now)) {
            window.callCount.set(0);
        }

        long calls = window.callCount.incrementAndGet();
        return calls <= annotation.calls() ? Result.CONTINUE : Result.SKIP;
    }
}
