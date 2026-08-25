import core.GameApplet;
import core.GameContext;
import player.Character;
import player.Player;
import processing.core.PApplet;
import state.GameOverState;
import state.MenuState;

public class BombIt extends GameApplet {

    private GameContext context;

    public static void main(String[] args) {
        PApplet.main("BombIt");
    }

    @Override
    public void settings() {
        context = new GameContext(this);
        size(1920, 1080);
//        fullScreen();

        context.getStateManager().setState(
                new MenuState(context)
        );
    }

    @Override
    public void draw() {
        context.getViewport().update(width, height);

        background(0);

        pushMatrix();

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
    public void mousePressed() {
        context.getStateManager().mousePressed(
                mouseX,
                mouseY
        );
    }
}
