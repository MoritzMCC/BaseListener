package de.MoritzMCC.baseListener;

import de.MoritzMCC.Main;
import de.MoritzMCC.anntotations.AnnotationHandler;
import de.MoritzMCC.anntotations.AnnotationRegestry;
import de.MoritzMCC.anntotations.Async;
import de.MoritzMCC.anntotations.Listen;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class BaseListener {

    private static final Map<Class<? extends Annotation>, AnnotationHandler<?>> globalAnnotationHandlers = new HashMap<>();
    protected ThreadLocal<Player> player = new ThreadLocal<>();

    public static <A extends Annotation> void registerAnnotationHandler(Class<A> annotationClass, AnnotationHandler<A> handler) {
        globalAnnotationHandlers.put(annotationClass, handler);
    }

    private final Map<Class<? extends Event>, List<Method>> handlers = new HashMap<>();

    protected BaseListener() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Listen.class))continue;
            if (method.getParameterCount() != 1) continue;
            if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) continue;

            Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];

            handlers.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(method);
        }

        EventManager.getInstance().addListener(this);
    }

    public void dispatch(Event event) {
        for (Map.Entry<Class<? extends Event>, List<Method>> entry : handlers.entrySet()) {

            if (!entry.getKey().isAssignableFrom(event.getClass())) continue;

            for (Method method : entry.getValue()) {
                try {
                    boolean proceed = true;
                    boolean async = false;
                    System.out.println("Annotations on " + method.getName() + ": " + Arrays.toString(method.getAnnotations()));

                    for (Annotation ann : method.getAnnotations()) {
                        if (ann instanceof Async) {
                            async = true;
                            continue;
                        }
                        Main.getInstance().getLogger().info(ann.annotationType().getSimpleName());
                        AnnotationHandler handler = AnnotationRegestry.getHandler(ann.annotationType());

                        if (handler != null) {
                            proceed = handler.handle(ann, event, method);
                            if (!proceed) break;
                        }
                    }

                    if (!proceed) continue;

                    method.setAccessible(true);


                    if (async) {
                        Bukkit.getScheduler().runTaskAsynchronously(
                                EventManager.getInstance().getPlugin(),
                                () -> {
                                    try {
                                        extractPlayer(event);
                                        method.invoke(this, event);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }finally {
                                        player.remove();
                                    }
                                }
                        );
                    } else {
                        extractPlayer(event);
                        method.invoke(this, event);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    player.remove();
                }
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
}