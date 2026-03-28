package de.MoritzMCC.example;

import de.MoritzMCC.anntotations.AnnotationRegestry;
import de.MoritzMCC.baseListener.EventManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
   static Main instance;
    EventManager eventManager;
    @Override
    public void onEnable() {
        instance = this;
        eventManager = new EventManager(this); //before Listeners
        registerCustomAnnotations(); // before Listeners
        new ExampleListener();

    }

    private void registerCustomAnnotations(){
        AnnotationRegestry.builder()
                .register(ExampleAnnotation.class, new ExampleAnnotationHandler())
                .build();
        /*
        or AnnotationRegestry.registerHandler(ExampleAnnotation.class, new ExampleAnnotationHandler());
         */
    }

}
