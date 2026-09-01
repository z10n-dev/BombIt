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

  public void keyPressed(char key, int keyCode) {
    if (currentState != null) {
      currentState.keyPressed(key, keyCode);
    }
  }

  public void keyReleased(char key, int keyCode) {
    if (currentState != null) {
      currentState.keyReleased(key, keyCode);
    }
  }

  public void keyTyped(char key) {
    if (currentState != null) {
      currentState.keyTyped(key);
    }
  }

  public void mousePressed(int mouseX, int mouseY) {
    if (currentState != null) {
      currentState.mousePressed(mouseX, mouseY);
    }
  }
}
