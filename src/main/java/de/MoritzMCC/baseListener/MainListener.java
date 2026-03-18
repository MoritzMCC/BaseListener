package de.MoritzMCC.baseListener;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class MainListener implements Listener {

    private final Plugin plugin;

    public MainListener(Plugin plugin) {
        this.plugin = plugin;
        registerAllEvents();
    }

    private void registerAllEvents() {
        // Liste der registrierbaren Events (Bukkit 1.21.11)
        Class<? extends Event>[] events = new Class[]{
                // Player Events
                org.bukkit.event.player.AsyncPlayerChatEvent.class,
                org.bukkit.event.player.AsyncPlayerPreLoginEvent.class,
                org.bukkit.event.player.PlayerJoinEvent.class,
                org.bukkit.event.player.PlayerQuitEvent.class,
                org.bukkit.event.player.PlayerRespawnEvent.class,
                org.bukkit.event.player.PlayerMoveEvent.class,
                org.bukkit.event.player.PlayerInteractEvent.class,
                org.bukkit.event.player.PlayerInteractEntityEvent.class,
                org.bukkit.event.player.PlayerAnimationEvent.class,
                org.bukkit.event.player.PlayerDropItemEvent.class,
                org.bukkit.event.player.PlayerPickupItemEvent.class,
                org.bukkit.event.player.PlayerExpChangeEvent.class,
                org.bukkit.event.player.PlayerLevelChangeEvent.class,
                org.bukkit.event.player.PlayerItemConsumeEvent.class,
                org.bukkit.event.player.PlayerItemDamageEvent.class,
                org.bukkit.event.player.PlayerGameModeChangeEvent.class,
                org.bukkit.event.player.PlayerBucketEmptyEvent.class,
                org.bukkit.event.player.PlayerBucketFillEvent.class,
                // Block Events
                org.bukkit.event.block.BlockBreakEvent.class,
                org.bukkit.event.block.BlockPlaceEvent.class,
                org.bukkit.event.block.BlockBurnEvent.class,
                org.bukkit.event.block.BlockFadeEvent.class,
                org.bukkit.event.block.BlockFormEvent.class,
                org.bukkit.event.block.BlockGrowEvent.class,
                org.bukkit.event.block.BlockIgniteEvent.class,
                org.bukkit.event.block.LeavesDecayEvent.class,
                org.bukkit.event.block.SignChangeEvent.class,
                // Entity Events
                org.bukkit.event.entity.CreatureSpawnEvent.class,
                org.bukkit.event.entity.EntityDamageEvent.class,
                org.bukkit.event.entity.EntityDeathEvent.class,
                org.bukkit.event.entity.EntityExplodeEvent.class,
                org.bukkit.event.entity.FoodLevelChangeEvent.class,
                org.bukkit.event.entity.PlayerDeathEvent.class,
                // Inventory Events
                org.bukkit.event.inventory.InventoryClickEvent.class,
                org.bukkit.event.inventory.InventoryCloseEvent.class,
                org.bukkit.event.inventory.InventoryOpenEvent.class,
                org.bukkit.event.inventory.CraftItemEvent.class,
                org.bukkit.event.inventory.PrepareItemCraftEvent.class,
                // World / Chunk Events
                org.bukkit.event.world.ChunkLoadEvent.class,
                org.bukkit.event.world.ChunkUnloadEvent.class,
                org.bukkit.event.world.WorldLoadEvent.class,
                org.bukkit.event.world.WorldUnloadEvent.class,
                org.bukkit.event.world.WorldSaveEvent.class
        };

        // Registrierung aller Events
        for (Class<? extends Event> eventClass : events) {
            try {
                Bukkit.getServer().getPluginManager().registerEvent(
                        eventClass,
                        this,
                        EventPriority.NORMAL,
                        (listener, event) -> {
                            // Async Events auf Hauptthread verschieben
                            if (event.isAsynchronous()) {
                                Bukkit.getScheduler().runTask(plugin, () ->
                                        EventManager.getInstance().dispatchEvent(event));
                            } else {
                                EventManager.getInstance().dispatchEvent(event);
                            }
                        },
                        plugin
                );
            } catch (Exception e) {
                plugin.getLogger().warning("Konnte Event nicht registrieren: " + eventClass.getName());
            }
        }

        plugin.getLogger().info("MainListener hat " + events.length + " Events registriert.");
    }
}