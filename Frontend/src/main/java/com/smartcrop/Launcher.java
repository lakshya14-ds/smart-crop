package com.smartcrop;

/**
 * Launcher class required when packaging as a fat/uber JAR.
 * This works around the JavaFX module system restriction that prevents
 * launching directly from a JAR whose main class extends Application.
 *
 * Run with: java -jar smart-crop-advisory-1.0.0-fat.jar
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
