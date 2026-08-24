package core;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private final PApplet app;
    private final Map<String, PImage> images = new HashMap<>();
    private static final String ASSETS_PATH = "drawable/";

    public AssetManager(PApplet app) {
        this.app = app;
    }

    public PImage loadImage(String name) {
        return images.computeIfAbsent(name, assetName -> {
            PImage image = app.loadImage(ASSETS_PATH + assetName);

            if (image == null) {
                throw new RuntimeException("Asset not found or invalid: " + assetName);
            }

            return image;
        });
    }

}
