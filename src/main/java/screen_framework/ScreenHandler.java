package screen_framework;

import application.GameApp;
import menu.MenuScreen;
import game.GameScreen;

public class ScreenHandler{
    private final GameApp app;
    private Screen currentScreen = null;

    public ScreenHandler(GameApp app){
        this.app = app;
        initialize();
    }

    private void initialize() {
        // Set the initial screen to the menu screen
        setCurrentScreen(ScreenName.MENU);
    }

    public void setCurrentScreen(ScreenName screenName) {
        app.background(10, 18, 35);


        this.currentScreen = switch (screenName) {
            case ScreenName.MENU -> new MenuScreen(this, app);
            case ScreenName.GAME -> new GameScreen(this, app);
        };
    }

    public void update() {
        if (currentScreen != null) {
            currentScreen.update();
        }
    }

    public void draw() {
        if (currentScreen != null) {
            currentScreen.draw();
        }
    }

    public void mousePressed(int x, int y) {
        if (currentScreen != null) {
            currentScreen.mousePressed(x, y);
        }
    }

    public void keyPressed(char key) {
        if (currentScreen != null) {
            currentScreen.keyPressed(key);
        }
    }
}
