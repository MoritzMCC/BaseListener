package de.MoritzMCC.anntotations.impl;

import de.MoritzMCC.anntotations.CancelCondition;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;

public class PlayerSneakCondition implements CancelCondition<Event> {
    @Override
    public boolean check(Event event) {
        if (!(event instanceof PlayerEvent playerEvent))return false;
        return playerEvent.getPlayer().isSneaking();
    }
}
