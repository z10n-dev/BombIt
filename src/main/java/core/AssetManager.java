package core;

import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.SoundFile;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private final PApplet app;
    private final Map<String, PImage> images = new HashMap<>();
    private final Map<String, SoundFile> sounds = new HashMap<>();
    private static final String IMAGE_PATH = "drawable/";
    private static final String SOUND_PATH = "sound/";

    public AssetManager(PApplet app) {
        this.app = app;
    }

    public PImage loadImage(String name) {
        return images.computeIfAbsent(name, assetName -> {
            PImage image = app.loadImage(IMAGE_PATH + assetName);

            if (image == null) {
                throw new RuntimeException("Asset not found or invalid: " + assetName);
            }

            return image;
        });
    }

    public SoundFile loadSound(String name) {
        return sounds.computeIfAbsent(
                name,
                assetName -> new SoundFile(
                        app,
                        SOUND_PATH + assetName
                )
        );
    }

}
