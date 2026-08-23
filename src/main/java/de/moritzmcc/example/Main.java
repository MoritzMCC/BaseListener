package de.moritzmcc.example;

import de.moritzmcc.annotations.AnnotationRegistry;
import de.moritzmcc.baseListener.EventManager;
import de.moritzmcc.events.EventRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private EventManager eventManager;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerCustomEvents();
        eventManager = new EventManager(this);
        registerCustomAnnotations();
        new ExampleListener();
    }

    private void registerCustomAnnotations() {
        AnnotationRegistry.builder()
                .register(ExampleAnnotation.class, new ExampleAnnotationHandler())
                .build();
    }

    private void registerCustomEvents() {
        EventRegistry.registerCustomEvent(ExampleCustomEvent.class);
    }
}
