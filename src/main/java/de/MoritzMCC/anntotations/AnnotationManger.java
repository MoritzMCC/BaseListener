package de.MoritzMCC.anntotations;

import de.MoritzMCC.anntotations.annotation.*;
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
import java.util.function.Function;

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
                .register(isEntityType.class, this::isEntityType)
                .register(Gamemode.class, this::hasGamemode)
                .register(Holding.class, this::holdsItem)
                .build();
    }

    private Result cancelIf(cancelIf annotation, Event event, Method method){
        if (!(event instanceof Cancellable cancelEvent))return Result.CONTINUE;
        try {
            CancelCondition<Event> condition = (CancelCondition<Event>) (annotation).condition().getDeclaredConstructor().newInstance();
            if (condition.check(event) || annotation.cancel()){
                return Result.CANCEL;
            }

        }catch (Exception e){
            e.printStackTrace();
        }return Result.CONTINUE;
    }

    private Result log(Log annotation, Event event, Method method){
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

        return Result.CONTINUE;
    }

    private Result permission(Permission annotation, Event event, Method method){
        return Result.get ((event instanceof PlayerEvent playerEvent) &&  playerEvent.getPlayer().hasPermission(annotation.permission()));
    }

    private Result requiresPlayer(requiresPlayer annotation, Event event, Method method){
        return withPlayer(event, p -> Result.CONTINUE);
    }

    private Result isEntityType(isEntityType annotation, Event event, Method method){
        return Result.get(event instanceof EntityEvent entityEvent
                && entityEvent.getEntityType().equals(annotation.value()));
    }


    private Player extractPlayer(Event event){
        if (event instanceof PlayerEvent playerEvent) {
            return playerEvent.getPlayer();
        }else if (event instanceof EntityEvent entityEvent && entityEvent.getEntityType().equals(EntityType.PLAYER)) {
            return  (Player) entityEvent.getEntity();
        }else {
            return null;
        }
    }
    private Result withPlayer(Event event, Function<Player,Result> action){
        Player player = extractPlayer(event);
        if (player == null)return Result.SKIP;
        return action.apply(player);
    }

    private Result hasGamemode(Gamemode annotation, Event event, Method method){
        return withPlayer(event, player ->
                Result.get(player.getGameMode().equals(annotation.value())));
    }

    private Result holdsItem(Holding annotation, Event event, Method method){
        return withPlayer(event, player ->
                player.getInventory().getItemInMainHand().getType().equals(annotation.value()) ? Result.CONTINUE : Result.SKIP);
    }
}
