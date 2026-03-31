package de.MoritzMCC.baseListener;

import de.MoritzMCC.anntotations.AnnotationHandler;
import de.MoritzMCC.anntotations.AnnotationRegestry;
import de.MoritzMCC.anntotations.Result;
import de.MoritzMCC.anntotations.annotation.Async;
import de.MoritzMCC.anntotations.annotation.Delay;
import de.MoritzMCC.anntotations.annotation.Listen;
import de.MoritzMCC.parameterresolver.ParameterManager;
import de.MoritzMCC.parameterresolver.ParameterResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public abstract class BaseListener {

    protected ThreadLocal<Player> player = new ThreadLocal<>();

    private final Map<Class<? extends Event>, List<HandlerMeta>> handlerMap = new HashMap<>();

    protected BaseListener() {

        for (Method method : this.getClass().getDeclaredMethods()) {

            if (!method.isAnnotationPresent(Listen.class)) continue;
            if (method.getParameterCount() == 0) continue;

            boolean hasEvent = false;
            for (Class<?> param : method.getParameterTypes()) {
                if (Event.class.isAssignableFrom(param)) {
                    hasEvent = true;
                    break;
                }
            }
            if (!hasEvent) continue;

            Class<? extends Event> eventClass = null;

            for (Class<?> param : method.getParameterTypes()) {
                if (Event.class.isAssignableFrom(param)) {
                    eventClass = (Class<? extends Event>) param;
                    break;
                }
            }

            if (eventClass == null) continue;

            method.setAccessible(true);

            handlerMap
                    .computeIfAbsent(eventClass, k -> new ArrayList<>())
                    .add(new HandlerMeta(method));
        }

        EventManager.getInstance().addListener(this);
    }

    public void dispatch(Event event) {

        for (Map.Entry<Class<? extends Event>, List<HandlerMeta>> entry : handlerMap.entrySet()) {

            if (!entry.getKey().isAssignableFrom(event.getClass())) continue;

            for (HandlerMeta meta : entry.getValue()) {

                try {
                    Result result = Result.CONTINUE;
                    int delay = 0;

                    for (Annotation ann : meta.annotations) {

                        if (ann instanceof Async) continue;

                        if (ann instanceof Delay d) {
                            delay += d.ticks();
                            continue;
                        }

                        AnnotationHandler handler =
                                AnnotationRegestry.getHandler(ann.annotationType());

                        if (handler != null) {
                            result = handler.handle(ann, event, meta.method);

                            if (result.shouldCancel() && event instanceof Cancellable cancellable) {
                                cancellable.setCancelled(true);
                            }

                            if (!result.shouldContinue()) break;
                        }
                    }

                    if (!result.shouldContinue()) continue;

                    Runnable task = () -> {
                        extractPlayer(event);
                        Object[] args = resolveParameters(meta.method, event);
                        invoke(meta.method, args, event);
                    };

                    if (meta.async) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(
                                EventManager.getInstance().getPlugin(),
                                task,
                                delay
                        );
                    } else {
                        Bukkit.getScheduler().runTaskLater(
                                EventManager.getInstance().getPlugin(),
                                task,
                                delay
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Player getPlayer() {
        return player.get();
    }

    private void extractPlayer(Event event) {
        if (event instanceof PlayerEvent playerEvent) {
            player.set(playerEvent.getPlayer());
        } else if (event instanceof EntityEvent entityEvent && entityEvent.getEntity() instanceof Player p) {
            player.set(p);
        } else {
            player.remove();
        }
    }

    private void invoke(Method method, Object[] args, Event event) {
        try {
            extractPlayer(event);
            method.invoke(this, args);

        } catch (InvocationTargetException e) {
            System.err.println("=== Listener Error ===");
            e.getCause().printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            player.remove();
        }
    }

    private Object[] resolveParameters(Method method, Event event) {

        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {

            Class<?> type = params[i].getType();

            if (type.isAssignableFrom(event.getClass())) {
                args[i] = event;
                continue;
            }

            ParameterResolver resolver = ParameterManager.resolve(type);

            if (resolver != null) {
                args[i] = resolver.resolve(event, this);
                continue;
            }

            throw new IllegalStateException("No resolver found for parameter: " + type.getName());
        }

        return args;
    }

    private static class HandlerMeta {
        final Method method;
        final boolean async;
        final List<Annotation> annotations;

        public HandlerMeta(Method method) {
            this.method = method;
            this.async = method.isAnnotationPresent(Async.class);
            this.annotations = Arrays.asList(method.getAnnotations());
        }
    }
}