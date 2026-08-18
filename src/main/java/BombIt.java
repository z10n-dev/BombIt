import processing.core.PApplet;
import screen_framework.ScreenHandler;

public class BombIt extends PApplet {

    private ScreenHandler screenHandler = new ScreenHandler();

    public static void main(String[] args) {
        PApplet.main("BombIt");
    }

    @Override
    public void draw() {
        screenHandler.update();
        screenHandler.draw();
    }

    @Override
    public void keyPressed() {
        screenHandler.keyPressed(key);
    }

    @Override
    public void keyReleased() {
        screenHandler.keyReleased(key);
    }

    @Override
    public void mousePressed() {
        screenHandler.mousePressed(
                mouseX,
                mouseY
        );
    }
}
