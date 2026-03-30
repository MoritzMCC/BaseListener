package de.MoritzMCC.example;

import de.MoritzMCC.anntotations.annotation.*;
import de.MoritzMCC.anntotations.impl.PlayerSneakCondition;
import de.MoritzMCC.baseListener.BaseListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;


/**
 * Example for a BaseListener implementation
 */
public class ExampleListener extends BaseListener {

    public ExampleListener() {
        super();
    }


    @Listen
    @Async
    public void onJoin(PlayerJoinEvent event) {
        //runs async
        event.setJoinMessage("HALLO HALLO");
    }

    @Listen
    @cancelIf(condition = PlayerSneakCondition.class)
    public void onMove(PlayerMoveEvent event) {
        // something whatever you want
    }

    @Listen
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.getPlayer().sendMessage("hi");
    }

    @Listen
    public void onEggThrow(PlayerEggThrowEvent event) {
        event.getPlayer().sendMessage("hi__");
        Bukkit.getPluginManager().callEvent(new ExampleCustomEvent(getPlayer()));
    }

    @Listen
    @requiresPlayer //only executes Methode if event is instance of PlayerEvent or Entity is a Player
    public void onEntityDamage(EntityDamageEvent event) {
        getPlayer().sendMessage("you took damage"); // getPlayer() -> event.getPlayer() or (Player) entity
        //getPlayer() can be null if event has no PLayer or Entity so using @requiresPlayer is recommended
    }

    @Listen
    @isEntityType(EntityType.ZOMBIE)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Zombie zombie = (Zombie) event.getEntity();
        zombie.setCustomName("Peter");
    }

    @Listen
    @Limit(limit = 3, resetAfter = 5) //cancels event if possible and not executing methode when event trys more than limit times in resetAfter seconds
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        getPlayer().sendMessage("sleep well");
    }

    @Listen
    @requiresPlayer
    public void onExampleEvent(ExampleCustomEvent event) {
        getPlayer().sendMessage("custom event");
    }


}
