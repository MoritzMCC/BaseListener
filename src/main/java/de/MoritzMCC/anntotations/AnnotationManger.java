package de.MoritzMCC.anntotations;

import de.MoritzMCC.anntotations.impl.CooldownHandler;
import de.MoritzMCC.anntotations.impl.LimitHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Method;

public class AnnotationManger {
    private final Plugin plugin;

    public AnnotationManger(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(){
        AnnotationRegestry.builder()
                .register(cancelIf.class, (this::cancelIf))
                .register(Log.class, (this::log))
                .register(Permission.class, this::permission)
                .register(Cooldown.class, new CooldownHandler())
                .register(Limit.class, new LimitHandler())
                .register(requiresPlayer.class, this::requiresPlayer)
                .build();
    }

    private boolean cancelIf(cancelIf annotation, Event event, Method method){
        if (!(event instanceof Cancellable cancelEvent))return true;
        try {
            CancelCondition<Event> condition = (CancelCondition<Event>) (annotation).condition().getDeclaredConstructor().newInstance();
            if (condition.check(event) || annotation.cancel()){
                cancelEvent.setCancelled(true);
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }return true;
    }

    private boolean log(Log annotation, Event event, Method method){
        StringBuilder sb = new StringBuilder();

        if (event instanceof BlockEvent be && annotation.blockType())sb.append("[block type] ").append(be.getBlock().getType().name());
        if (annotation.eventName())sb.append("[event name] ").append(event.getClass().getSimpleName());

        if (event instanceof PlayerEvent playerEvent) {
            if (annotation.playerName()) sb.append("[player name] ").append(playerEvent.getPlayer().getName());
            if (annotation.id()) sb.append("[event id] ").append(playerEvent.getPlayer().getUniqueId());
            if (annotation.location()) sb.append("[event location] ").append(playerEvent.getPlayer().getLocation());
        }
        sb.append(annotation.message());
        plugin.getLogger().info(sb.toString());

        return true;
    }

    private boolean permission(Permission annotation, Event event, Method method){
        return (event instanceof PlayerEvent playerEvent) &&  playerEvent.getPlayer().hasPermission(annotation.permission());
    }

    private boolean requiresPlayer(requiresPlayer annotation, Event event, Method method){

        Player player = null;
        if (event instanceof PlayerEvent playerEvent) {
            player = playerEvent.getPlayer();
        }else if (event instanceof EntityEvent entityEvent && entityEvent.getEntityType().equals(EntityType.PLAYER)) {
            player = (Player) entityEvent.getEntity();
        }else {
            return false;
        }
        setPlayer(player,method);
        return true;
    }

    private void setPlayer(Player player, Method method){
        try {
           Method setPlayerListener = method.getDeclaringClass().getMethod("setPlayer",Player.class);
           setPlayerListener.invoke(player,player);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
