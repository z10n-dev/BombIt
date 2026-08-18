package assets;

import processing.core.PApplet;
import processing.core.PImage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AssetManager {
    private final PApplet app;
    private final Map<String, PImage> images = new HashMap<>();

    public AssetManager(PApplet app) {
        this.app = app;
    }

    public PImage getImage(String filename) {
        return images.computeIfAbsent(filename, this::loadImage);
    }

    private PImage loadImage(String filename) {
        URL resource = Objects.requireNonNull(
                AssetManager.class.getResource("/drawable/" + filename),
                "Image not found: " + filename
        );
        String path = resource.toExternalForm();
        if (!path.contains(".jar!")) {
            path = path.replaceFirst("^file:", "");
        }
        return Objects.requireNonNull(
                app.loadImage(path),
                "Could not load image: " + filename
        );
    }
}
