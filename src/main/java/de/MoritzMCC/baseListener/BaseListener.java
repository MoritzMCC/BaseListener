package de.MoritzMCC.baseListener;

import de.MoritzMCC.anntotations.AnnotationHandler;
import de.MoritzMCC.anntotations.AnnotationRegestry;
import de.MoritzMCC.anntotations.Result;
import de.MoritzMCC.anntotations.annotation.Async;
import de.MoritzMCC.anntotations.annotation.Delay;
import de.MoritzMCC.anntotations.annotation.Listen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public abstract class BaseListener {

    private static final Map<Class<? extends Annotation>, AnnotationHandler<?>> globalAnnotationHandlers = new HashMap<>();
    protected ThreadLocal<Player> player = new ThreadLocal<>();

    public static <A extends Annotation> void registerAnnotationHandler(Class<A> annotationClass, AnnotationHandler<A> handler) {
        globalAnnotationHandlers.put(annotationClass, handler);
    }

    private final Map<Class<? extends Event>, List<Method>> handlers = new HashMap<>();
    private final Map<Class<? extends Event>, List<HandlerMeta>> handlerMap = new HashMap<>();

    protected BaseListener() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Listen.class))continue;
            if (method.getParameterCount() != 1) continue;
            if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) continue;

            Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];

            method.setAccessible(true);
            handlerMap
                    .computeIfAbsent(eventClass, k -> new ArrayList<>())
                    .add(new HandlerMeta(method));
        }

        EventManager.getInstance().addListener(this);
    }

    public void dispatch(Event event) {

        List<HandlerMeta> metas = handlerMap.get(event.getClass());

        if (metas == null) return;
        for (HandlerMeta meta : metas) {
            try {
                Result result = Result.CONTINUE;
                int delay = 0;
                for (Annotation ann : meta.annotations) {
                    if (ann instanceof Async) continue;
                    if (ann instanceof Delay delay1){
                        delay += delay1.ticks();
                        continue;
                    }

                    AnnotationHandler handler =
                            AnnotationRegestry.getHandler(ann.annotationType());

                    if (handler != null) {
                        result = handler.handle(ann, event, meta.method);
                        if (result.shouldCancel() && event instanceof Cancellable cancellable){
                            cancellable.setCancelled(true);
                        }
                        if (!result.shouldContinue()) break;
                    }
                }

                if (!result.shouldContinue())continue;

                if (meta.async) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(
                            EventManager.getInstance().getPlugin(),
                            () -> invoke(meta.method, event), delay
                    );
                } else {
                    Bukkit.getScheduler().runTaskLater(
                        EventManager.getInstance().getPlugin(),
                            () -> invoke(meta.method, event), delay
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    protected Player getPlayer() {
        return player.get();
    }

    private void extractPlayer(Event event) {
        if (event instanceof PlayerEvent playerEvent) player.set(playerEvent.getPlayer());
        else if (event instanceof EntityEvent entityEvent && entityEvent.getEntity() instanceof Player p)player.set(p);
        else player.remove();
    }

    private void invoke(Method method, Event event) {
        try {
            extractPlayer(event);
            method.invoke(this, event);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            player.remove();
        }
    }

    private static class HandlerMeta{
        final Method method;
        final boolean async;
        final List<Annotation> annotations;

        public HandlerMeta(Method method) {
            this.method = method;
            async = method.isAnnotationPresent(Async.class);
            annotations = Arrays.asList(method.getAnnotations());
        }
    }

}