package de.moritzmcc.parameterresolver.impl;

import de.moritzmcc.baseListener.BaseListener;
import de.moritzmcc.parameterresolver.ParameterResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.lang.reflect.Parameter;

public class PlayerResolver implements ParameterResolver {

    @Override
    public boolean canResolve(Parameter parameter) {
        return parameter.getType().equals(Player.class);
    }

    @Override
    public Object resolve(Parameter parameter, Event event, BaseListener listener) {
        return listener.getPlayer();
    }
}
