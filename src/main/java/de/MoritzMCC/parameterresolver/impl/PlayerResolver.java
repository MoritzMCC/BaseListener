package de.MoritzMCC.parameterresolver.impl;

import de.MoritzMCC.baseListener.BaseListener;
import de.MoritzMCC.parameterresolver.ParameterResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PlayerResolver implements ParameterResolver {
    @Override
    public boolean canResolve(Class<?> type) {
        return type.equals(Player.class);
    }

    @Override
    public Object resolve(Event event, BaseListener listener) {
        return listener.getPlayer();
    }
}
