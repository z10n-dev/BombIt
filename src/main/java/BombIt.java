import application.GameApp;
import processing.core.PApplet;
import screen_framework.ScreenHandler;
import settings.Settings;

public class BombIt extends GameApp {

    private ScreenHandler screenHandler;

    public static void main(String[] args) {
        PApplet.main("BombIt");
    }

    @Override
    public void settings() {
        size(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
    }

    @Override
    public void setup() {
        screenHandler = new ScreenHandler(this);
    }

    @Override
    public void draw() {
        screenHandler.update();

        pushMatrix();
        scale(Settings.SCALE_FACTOR);
        screenHandler.draw();
        popMatrix();
    }

    @Override
    public void keyPressed() {
        screenHandler.keyPressed(key);
    }

    @Override
    public void mousePressed() {
        screenHandler.mousePressed(
                Settings.toGameCoordinate(mouseX),
                Settings.toGameCoordinate(mouseY)
        );
    }
}
