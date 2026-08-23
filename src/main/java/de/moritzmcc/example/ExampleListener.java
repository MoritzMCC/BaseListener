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
 * Beispiel fuer eine BaseListener-Implementierung
 */
public class ExampleListener extends BaseListener {

    public ExampleListener() {
        super();
    }

    @Listen
    @Async
    public void onJoin(PlayerJoinEvent event) {
        // laeuft asynchron
        event.setJoinMessage("HALLO HALLO");
    }

    @Listen
    @CancelIf(condition = PlayerSneakCondition.class)
    public void onMove(PlayerMoveEvent event) {
        // beliebige Logik
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
    @RequiresPlayer // fuehrt die Methode nur aus, wenn das Event ein PlayerEvent ist oder die Entity ein Player ist
    public void onEntityDamage(EntityDamageEvent event) {
        getPlayer().sendMessage("you took damage"); // getPlayer() -> event.getPlayer() bzw. (Player) entity
        // getPlayer() kann null sein, wenn das Event keinen Player/keine Entity hat - daher @RequiresPlayer empfohlen
    }

    @Listen
    @IsEntityType(EntityType.ZOMBIE)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Zombie zombie = (Zombie) event.getEntity();
        zombie.setCustomName("Peter");
    }

    @Listen
    @Limit(limit = 3, resetAfter = 5) // cancelt das Event und verhindert die Ausfuehrung, wenn es haeufiger als "limit" mal in "resetAfter" Sekunden ausgeloest wird
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        getPlayer().sendMessage("sleep well");
    }

    @Listen
    @RequiresPlayer
    public void onExampleEvent(ExampleCustomEvent event) {
        getPlayer().sendMessage("custom event");
    }

    @Listen
    @Gamemode(GameMode.SURVIVAL)
    public void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        getPlayer().sendMessage("swap");
    }

    @Listen
    @Holding(Material.STICK)
    public void onPlayerInteract(PlayerInteractEvent event, @Inject Player player) {
        player.sendMessage("interact");
    }

    @Listen
    public void onTeleport(PlayerTeleportEvent event, @PlayersNearby(radius = 10) List<Player> nearbyPlayers) {
        nearbyPlayers.forEach(player -> player.sendMessage("someone teleported nearby"));
    }
}
