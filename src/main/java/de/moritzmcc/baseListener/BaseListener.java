package de.moritzmcc.baseListener;

import de.moritzmcc.annotations.AnnotationHandler;
import de.moritzmcc.annotations.AnnotationRegistry;
import de.moritzmcc.annotations.Result;
import de.moritzmcc.annotations.annotation.Async;
import de.moritzmcc.annotations.annotation.Delay;
import de.moritzmcc.annotations.annotation.Listen;
import de.moritzmcc.parameterresolver.ParameterManager;
import de.moritzmcc.parameterresolver.ParameterResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public abstract class BaseListener {

    private static final Map<Class<?>, List<HandlerMeta>> CLASS_METADATA_CACHE = new ConcurrentHashMap<>();

    protected final ThreadLocal<Player> player = new ThreadLocal<>();

    private final Map<Class<? extends Event>, List<HandlerMeta>> declaredHandlers = new HashMap<>();

    private final Map<Class<? extends Event>, List<HandlerMeta>> resolvedCache = new ConcurrentHashMap<>();

    protected BaseListener() {
        List<HandlerMeta> metas = CLASS_METADATA_CACHE.computeIfAbsent(getClass(), BaseListener::analyze);

        for (HandlerMeta meta : metas) {
            declaredHandlers.computeIfAbsent(meta.eventType, k -> new ArrayList<>()).add(meta);
            EventDispatchRegistry.getInstance().ensureRegistered(meta.eventType);
        }

        EventManager.getInstance().addListener(this);
    }

    @SuppressWarnings("unchecked")
    private static List<HandlerMeta> analyze(Class<?> listenerClass) {
        List<HandlerMeta> metas = new ArrayList<>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        for (Method method : listenerClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Listen.class)) continue;

            Parameter[] parameters = method.getParameters();
            Class<? extends Event> eventType = null;
            int eventParamIndex = -1;

            for (int i = 0; i < parameters.length; i++) {
                if (Event.class.isAssignableFrom(parameters[i].getType())) {
                    eventType = (Class<? extends Event>) parameters[i].getType();
                    eventParamIndex = i;
                    break;
                }
            }
            if (eventType == null) continue;

            method.setAccessible(true);

            try {
                MethodHandle handle = lookup.unreflect(method);
                metas.add(new HandlerMeta(method, handle, eventType, eventParamIndex));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Konnte MethodHandle fuer " + method + " nicht erzeugen", e);
            }
        }
        return metas;
    }

    public void dispatch(Event event) {
        List<HandlerMeta> handlers = resolvedCache.computeIfAbsent(event.getClass(), this::resolveHandlersFor);
        if (handlers.isEmpty()) return;

        for (HandlerMeta meta : handlers) {
            try {
                Result result = Result.CONTINUE;
                int delay = 0;

                for (Annotation ann : meta.annotations) {
                    if (ann instanceof Async) continue;

                    if (ann instanceof Delay d) {
                        delay += d.ticks();
                        continue;
                    }

                    AnnotationHandler<Annotation> handler = handlerFor(ann);
                    if (handler != null) {
                        result = handler.handle(ann, event, meta.method);

                        if (result.shouldCancel() && event instanceof Cancellable cancellable) {
                            cancellable.setCancelled(true);
                        }
                        if (!result.shouldContinue()) break;
                    }
                }

                if (!result.shouldContinue()) continue;

                Runnable task = () -> invoke(meta, event);

                if (meta.async) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(EventManager.getInstance().getPlugin(), task, delay);
                } else {
                    Bukkit.getScheduler().runTaskLater(EventManager.getInstance().getPlugin(), task, delay);
                }

            } catch (Exception e) {
                logSevere("An error occurred while dispatching an event " + event.getClass().getSimpleName(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private AnnotationHandler<Annotation> handlerFor(Annotation ann) {
        return (AnnotationHandler<Annotation>) AnnotationRegistry.getHandler(ann.annotationType());
    }

    private List<HandlerMeta> resolveHandlersFor(Class<? extends Event> runtimeType) {
        List<HandlerMeta> result = null;
        for (Map.Entry<Class<? extends Event>, List<HandlerMeta>> entry : declaredHandlers.entrySet()) {
            if (entry.getKey().isAssignableFrom(runtimeType)) {
                if (result == null) result = new ArrayList<>();
                result.addAll(entry.getValue());
            }
        }
        return result == null ? List.of() : result;
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

    private void invoke(HandlerMeta meta, Event event) {
        try {
            extractPlayer(event);
            Object[] args = resolveParameters(meta, event);
            meta.handle.invokeWithArguments(prepend(this, args));
        } catch (Throwable e) {
            logSevere("Fehler in Listener-Methode " + meta.method.getName(), e);
        } finally {
            player.remove();
        }
    }

    private static Object[] prepend(Object receiver, Object[] args) {
        Object[] full = new Object[args.length + 1];
        full[0] = receiver;
        System.arraycopy(args, 0, full, 1, args.length);
        return full;
    }

    private Object[] resolveParameters(HandlerMeta meta, Event event) {
        Object[] args = new Object[meta.parameters.length];

        for (int i = 0; i < meta.parameters.length; i++) {
            if (i == meta.eventParamIndex) {
                args[i] = event;
                continue;
            }

            ParameterResolver resolver = meta.resolvers[i];
            if (resolver == null) {
                throw new IllegalStateException("Kein Resolver fuer Parameter " + meta.parameters[i] + " in " + meta.method);
            }
            args[i] = resolver.resolve(meta.parameters[i], event, this);
        }
        return args;
    }

    private void logSevere(String message, Throwable t) {
        var logger = EventManager.getInstance().getPlugin().getLogger();
        logger.severe(message + (t.getMessage() != null ? ": " + t.getMessage() : ""));
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, message, t);
        }
    }

    private static final class HandlerMeta {
        final Method method;
        final MethodHandle handle;
        final Class<? extends Event> eventType;
        final int eventParamIndex;
        final boolean async;
        final List<Annotation> annotations;
        final Parameter[] parameters;
        final ParameterResolver[] resolvers;

        HandlerMeta(Method method, MethodHandle handle, Class<? extends Event> eventType, int eventParamIndex) {
            this.method = method;
            this.handle = handle;
            this.eventType = eventType;
            this.eventParamIndex = eventParamIndex;
            this.async = method.isAnnotationPresent(Async.class);
            this.annotations = List.of(method.getAnnotations());
            this.parameters = method.getParameters();
            this.resolvers = new ParameterResolver[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                if (i == eventParamIndex) continue;
                resolvers[i] = ParameterManager.resolve(parameters[i]);
            }
        }
    }
}
