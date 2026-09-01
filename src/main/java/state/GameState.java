package state;

import core.GameApplet;
import core.GameContext;

public abstract class GameState {

  protected final GameContext gameContext;
  protected final GameApplet app;

  public GameState(GameContext gameContext) {
    this.gameContext = gameContext;
    this.app = gameContext.getApp();
  }

  public abstract void update();

  public abstract void draw();

  public void keyPressed(char key, int keyCode) {}
  ;

  public void keyReleased(char key, int keyCode) {}
  ;

  public void keyTyped(char key) {}
  ;

  public void mousePressed(int mouseX, int mouseY) {}
  ;
}
