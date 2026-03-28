package de.MoritzMCC.example;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;


public class ExampleCustomEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public ExampleCustomEvent(Player who) {
        super(who);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
