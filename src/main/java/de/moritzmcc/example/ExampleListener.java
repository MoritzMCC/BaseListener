package de.moritzmcc.example;

import de.moritzmcc.annotations.annotation.*;
import de.moritzmcc.annotations.annotation.parameter.Inject;
import de.moritzmcc.annotations.annotation.parameter.PlayersNearby;
import de.moritzmcc.annotations.impl.PlayerSneakCondition;
import de.moritzmcc.baseListener.BaseListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.List;

/**
 * Example listener demonstrating the different features provided by the
 * annotation-based event system.
 *
 * <p>
 * This class shows how events can be handled using annotations instead of
 * manually registering individual Bukkit event listeners. Annotations can be
 * used to add conditions, inject parameters, run handlers asynchronously,
 * limit event executions, and access nearby players or the affected player.
 * </p>
 */
public class ExampleListener extends BaseListener {

    public ExampleListener() {
        super();
    }

    @Listen
    @Async
    public void onJoin(PlayerJoinEvent event) {
        // Runs the event handler asynchronously.
        event.setJoinMessage("HALLO HALLO");
    }

    @Listen
    @CancelIf(condition = PlayerSneakCondition.class)
    public void onMove(PlayerMoveEvent event) {
        // The handler is cancelled when the specified condition is met.
        // Add any movement-related logic here.
    }

    @Listen
    public void onInventoryOpen(InventoryOpenEvent event) {
        // Sends a message to the player when they open an inventory.
        event.getPlayer().sendMessage("hi");
    }

    @Listen
    public void onEggThrow(PlayerEggThrowEvent event) {
        event.getPlayer().sendMessage("hi__");

        // Manually triggers a custom Bukkit event.
        Bukkit.getPluginManager().callEvent(new ExampleCustomEvent(getPlayer()));
    }

    @Listen
    @RequiresPlayer
    public void onEntityDamage(EntityDamageEvent event) {
        // Only runs when the event is associated with a player.
        getPlayer().sendMessage("you took damage");

        // getPlayer() returns the player associated with the event.
        // It can return null if the event does not have a player or entity.
        // Therefore, @RequiresPlayer is recommended when using getPlayer().
    }

    @Listen
    @IsEntityType(EntityType.ZOMBIE)
    public void onEntitySpawn(EntitySpawnEvent event) {
        // Only handles spawn events for zombies.
        Zombie zombie = (Zombie) event.getEntity();
        zombie.setCustomName("Peter");
    }

    @Listen
    @Limit(limit = 3, resetAfter = 5)
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        // Limits the handler to 3 executions within 5 seconds.
        // Further executions are cancelled until the limit resets.
        getPlayer().sendMessage("sleep well");
    }

    @Listen
    @RequiresPlayer
    public void onExampleEvent(ExampleCustomEvent event) {
        // Only runs when the custom event has an associated player.
        getPlayer().sendMessage("custom event");
    }

    @Listen
    @Gamemode(GameMode.SURVIVAL)
    public void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        // Only runs when the player is in Survival mode.
        getPlayer().sendMessage("swap");
    }

    @Listen
    @Holding(Material.STICK)
    public void onPlayerInteract(PlayerInteractEvent event, @Inject Player player) {
        // Only runs when the player is holding a stick.
        // @Inject automatically provides the player associated with the event.
        player.sendMessage("interact");
    }

    @Listen
    public void onTeleport(
            PlayerTeleportEvent event,
            @PlayersNearby(radius = 10) List<Player> nearbyPlayers
    ) {
        // Automatically provides all players within a 10-block radius.
        nearbyPlayers.forEach(
                player -> player.sendMessage("someone teleported nearby")
        );
    }
}