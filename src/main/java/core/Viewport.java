package core;

import processing.core.PApplet;

public class Viewport {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;

    public float scale;
    public float offsetX;
    public float offsetY;

    public void update(int screenWidth, int screenHeight) {
        float scaleX = (float) screenWidth / WIDTH;
        float scaleY = (float) screenHeight / HEIGHT;
        scale = Math.min(scaleX, scaleY);

        offsetX = (screenWidth - WIDTH * scale) / 2;
        offsetY = (screenHeight - HEIGHT * scale) / 2;
    }

    public void apply(PApplet app) {
        app.translate(offsetX, offsetY);
        app.scale(scale);
    }

    public float screenToGameX(float x) {
        return (x - offsetX) / scale;
    }

    public float screenToGameY(float y) {
        return (y - offsetY) / scale;
    }
}
