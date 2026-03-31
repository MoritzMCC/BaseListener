package de.MoritzMCC.anntotations.annotation;

import org.bukkit.GameMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Gamemode {
    GameMode value();
}
