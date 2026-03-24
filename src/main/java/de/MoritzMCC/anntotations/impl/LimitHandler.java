package de.MoritzMCC.anntotations.impl;

import de.MoritzMCC.Main;
import de.MoritzMCC.anntotations.AnnotationHandler;
import de.MoritzMCC.anntotations.Limit;
import de.MoritzMCC.baseListener.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class LimitHandler implements AnnotationHandler<Limit> {
    Map<UUID,Map<String, LimitData>> limits = new ConcurrentHashMap<>();
    Map<String, LimitData> limitMethods = new ConcurrentHashMap<>();

    @Override
    public boolean handle(Limit annotation, Event event, Method method) {

        String key = method.toGenericString();
        Main.getInstance().getLogger().info("Limithandler called");

        switch (annotation.scope()) {
            case GLOBAL:
                LimitData global = limitMethods.computeIfAbsent(key, k -> new LimitData());
                if (global.count >= annotation.limit())return cancelEvent(event);
                global.count++;
                decrementSchedule(()->
                        {if (--global.count <= 0){
                                limitMethods.remove(key);
                        }},
                        annotation.resetAfter());
                return true;

            case PLAYER:
            default:
                if (!(event instanceof PlayerEvent playerEvent))return true;
                UUID uuid = playerEvent.getPlayer().getUniqueId();
                Map<String,LimitData> map = limits.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
                LimitData limit = map.computeIfAbsent(key, k -> new LimitData());
                if (limit.count >= annotation.limit())return cancelEvent(playerEvent);
                limit.count++;
                decrementSchedule(()-> {
                    if (--limit.count <= 0){
                        map.remove(key);
                        if (map.isEmpty()){
                            limits.remove(uuid);
                        }
                    }
                },annotation.resetAfter());
                return true;
        }
    }

    private void decrementSchedule(Runnable runnable, int seconds) {
        Bukkit.getScheduler().runTaskLater(EventManager.getInstance().getPlugin(), runnable, seconds * 20L);
    }

   static class LimitData{
        int count;
        long resetTimeStamp;
    }
}
