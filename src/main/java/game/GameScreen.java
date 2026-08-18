package game;

import application.GameApp;
import screen_framework.Screen;
import screen_framework.ScreenHandler;

public class GameScreen implements Screen {

    ScreenHandler screenHandler;
    GameApp app;

    public GameScreen(ScreenHandler screenHandler, GameApp app) {
        this.screenHandler = screenHandler;
        this.app = app;
        app.clear();
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {
        app.text("Test", 100, 100);
    }

    @Override
    public void mousePressed(int x, int y) {

    }

    @Override
    public void keyPressed(char key) {

    }
}
