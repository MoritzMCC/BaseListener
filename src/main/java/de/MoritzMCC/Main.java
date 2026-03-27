package de.MoritzMCC;

import de.MoritzMCC.baseListener.EventManager;
import de.MoritzMCC.baseListener.ExampleListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
   static Main instance;
    EventManager eventManager;
    @Override
    public void onEnable() {
        instance = this;
        eventManager = new EventManager(this);
        new ExampleListener();
    }
}
