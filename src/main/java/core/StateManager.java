package core;

import state.GameState;

public class StateManager {
    private GameState currentState;

    public void setState(GameState state) {
        this.currentState = state;
    }

    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }

    public void draw() {
        if (currentState != null) {
            currentState.draw();
        }
    }

    public void keyPressed(char key) {
        if (currentState != null) {
            currentState.keyPressed(key);
        }
    }

    public void keyReleased(char key) {
        if (currentState != null) {
            currentState.keyReleased(key);
        }
    }

    public void mousePressed(int mouseX, int mouseY) {
        if (currentState != null) {
            currentState.mousePressed(mouseX, mouseY);
        }
    }
}
