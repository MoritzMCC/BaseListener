package de.moritzmcc.parameterresolver.impl;

import de.moritzmcc.annotations.annotation.parameter.PlayersNearby;
import de.moritzmcc.baseListener.BaseListener;
import de.moritzmcc.parameterresolver.ParameterResolver;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.Parameter;
import java.util.List;

public class PlayersNearbyResolver implements ParameterResolver {

    @Override
    public boolean canResolve(Parameter parameter) {
        return parameter.getType().equals(List.class) && parameter.isAnnotationPresent(PlayersNearby.class);
    }

    @Override
    public Object resolve(Parameter parameter, Event event, BaseListener listener) {
        PlayersNearby annotation = parameter.getAnnotation(PlayersNearby.class);

        Entity entity;
        if (event instanceof PlayerEvent playerEvent) {
            entity = playerEvent.getPlayer();
        } else if (event instanceof EntityEvent entityEvent) {
            entity = entityEvent.getEntity();
        } else {
            return List.<Player>of();
        }

        double radius = annotation.radius();
        List<Player> nearbyPlayers = entity.getNearbyEntities(radius, radius, radius).stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .toList();

        return nearbyPlayers.size() < annotation.minPlayers() ? List.<Player>of() : nearbyPlayers;
    }
}
