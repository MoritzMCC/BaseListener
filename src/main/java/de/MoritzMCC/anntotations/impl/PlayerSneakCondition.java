package de.MoritzMCC.anntotations.impl;

import de.MoritzMCC.Main;
import de.MoritzMCC.anntotations.CancelCondition;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

public class PlayerSneakCondition implements CancelCondition<Event> {
    @Override
    public boolean check(Event event) {
        Main.getInstance().getLogger().info("eventtype: " + event.getClass().getName());
        if (!(event instanceof PlayerEvent playerEvent))return false;
        Main.getInstance().getLogger().info("sneak: " + playerEvent.getPlayer().isSneaking());
        return playerEvent.getPlayer().isSneaking();
    }
}
