package de.MoritzMCC.baseListener;

import de.MoritzMCC.anntotations.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExampleListener extends BaseListener{

    public ExampleListener() {
        super();
    }


    @Listen
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("HALLO HALLO");
    }


    public void onMove(PlayerMoveEvent event) {
        // something whatever you want
    }

    @Listen
    @Cooldown(seconds = 3)
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.getPlayer().sendMessage("hi");

    }

    @Listen
    @Cooldown(seconds = 3)
    public void onEggThrow(PlayerEggThrowEvent event) {
        event.getPlayer().sendMessage("hi__");

    }

    @Listen
    @requiresPlayer
    public void onEntityDamage(EntityDamageEvent event) {
        getPlayer().sendMessage("you took damage");
    }

    @Listen
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        getPlayer().sendMessage("sleep well");
    }

}
