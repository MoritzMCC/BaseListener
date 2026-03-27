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
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.getPlayer().sendMessage("hi");
    }

    @Listen
    public void onEggThrow(PlayerEggThrowEvent event) {
        event.getPlayer().sendMessage("hi__");
    }

    @Listen
    @requiresPlayer
    public void onEntityDamage(EntityDamageEvent event) {
        getPlayer().sendMessage("you took damage");
    }

    @Listen
    @Limit(limit = 3, resetAfter = 5) //if not cancellabel just don't execute methode
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        getPlayer().sendMessage("sleep well");
    }

}
