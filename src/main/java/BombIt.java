import core.GameApplet;
import core.GameContext;
import processing.core.PApplet;
import state.MenuState;

public class BombIt extends GameApplet {

  private GameContext context;

  public static void main(String[] args) {
    PApplet.main("BombIt");
  }

  @Override
  public void settings() {
    context = new GameContext(this);
    //         size(1920, 1080, P2D);
    fullScreen(P2D);

    context.getStateManager().setState(new MenuState(context));
  }

  @Override
  public void setup() {
    context.getTypography().setup();
    context.getViewport().update(width, height);
  }

  @Override
  public void draw() {

    pushMatrix();

    noSmooth();
    context.getViewport().apply(this);

    context.getStateManager().update();
    context.getStateManager().draw();

    popMatrix();
  }

  @Override
  public void keyPressed() {
    context.getStateManager().keyPressed(key, keyCode);
  }

  @Override
  public void keyReleased() {
    context.getStateManager().keyReleased(key, keyCode);
  }

  @Override
  public void keyTyped() {
    context.getStateManager().keyTyped(key);
  }

  @Override
  public void mousePressed() {
    context.getStateManager().mousePressed(mouseX, mouseY);
  }
}
