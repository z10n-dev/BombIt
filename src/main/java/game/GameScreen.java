package game;

import screen_framework.Screen;
import screen_framework.ScreenHandler;


public class GameScreen implements Screen {

    private final ScreenHandler screenHandler;

    public GameScreen(ScreenHandler screenHandler) {
        this.screenHandler = screenHandler;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {
    }

    private void drawHud() {
    }

    private void drawGameOver() {
    }

    @Override
    public void mousePressed(int x, int y) {

    }

    @Override
    public void keyPressed(char key) {

    }

    @Override
    public void keyReleased(char key) {

    }
}
