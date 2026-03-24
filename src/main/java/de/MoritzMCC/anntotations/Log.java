package de.MoritzMCC.anntotations;

public @interface Log {
    boolean playerName() default true;
    boolean eventName() default true;
    boolean location() default false;
    boolean blockType() default false;
    boolean id() default true;
    String message() default "";
}
