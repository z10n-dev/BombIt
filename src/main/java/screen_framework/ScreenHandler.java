package screen_framework;

import menu.MenuScreen;
import game.GameScreen;

public class ScreenHandler{
    private Screen currentScreen = null;

    public ScreenHandler(){
        initialize();
    }

    private void initialize() {
        // Set the initial screen to the menu screen
        setCurrentScreen(ScreenName.MENU);
    }

    public void setCurrentScreen(ScreenName screenName) {
        this.currentScreen = switch (screenName) {
            case ScreenName.MENU -> new MenuScreen(this);
            case ScreenName.GAME -> new GameScreen(this);
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

    public void keyReleased(char key) {
        if (currentScreen != null) {
            currentScreen.keyReleased(key);
        }
    }
}
