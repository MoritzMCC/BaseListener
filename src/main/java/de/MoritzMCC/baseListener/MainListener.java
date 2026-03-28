package de.MoritzMCC.baseListener;

import de.MoritzMCC.example.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

public class MainListener implements Listener {

    private final Plugin plugin;

    public MainListener(Plugin plugin) {
        this.plugin = plugin;
        registerAllEvents();
    }

    private void registerAllEvents() {

        for (Class<? extends Event> eventClass : new Reflections("org.bukkit.event").getSubTypesOf(Event.class)) {

             try {
                 Main.getInstance().getLogger().info("start registering event " + eventClass.getSimpleName());
                 if (java.lang.reflect.Modifier.isAbstract(eventClass.getModifiers())) continue;
                 eventClass.getMethod("getHandlerList");
                Bukkit.getServer().getPluginManager().registerEvent(
                        eventClass,
                        this,
                        EventPriority.NORMAL,
                        (listener, event) -> {
                            EventManager.getInstance().dispatchEvent(event);
                        },
                        plugin
                );
                Main.getInstance().getLogger().info("Registered event " + eventClass.getSimpleName());
             } catch (Exception e) {
                e.printStackTrace();
                Main.getInstance().getLogger().warning("Could not register event " + eventClass.getSimpleName());
             }
        }

    }
}