package pong;

import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.SoundFile;

import java.util.HashMap;
import java.util.Objects;

class FileLoader {
    private static final HashMap<String, PImage> imageCache = new HashMap<>();
    private static final HashMap<String, SoundFile> soundCache = new HashMap<>();

    public static String getPath(String filename){
        String path = Objects.requireNonNull(FileLoader.class.getResource("/" + filename)).toExternalForm();
        if(!path.contains(".jar!")) {
            path = path.replaceFirst("file:", "");
        }
        return Objects.requireNonNull(path);
    }

    public static PImage getImage(PApplet pApplet, String filename) {
        return imageCache.computeIfAbsent(filename, k ->  pApplet.loadImage(getPath("drawable/" + filename)));
    }

    public static SoundFile getSoundFile(PApplet pApplet, String filename) {
        return soundCache.computeIfAbsent(filename, k -> new SoundFile(pApplet, "sound/" + filename));
    }

    public static String[] getData(PApplet pApplet, String filename) {
        return pApplet.loadStrings("data/" + filename);
    }

}
