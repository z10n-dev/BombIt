package state;

import core.GameContext;
import core.Viewport;
import player.Player;
import processing.core.PImage;
import style.Colors;
import ui.Button;
import ui.Typography;

public class GameOverState extends GameState {

    private final Player winner;
    private final String resultMessage;
    private final Button backButton;

    public GameOverState(GameContext gameContext, Player winner, String resultMessage) {
        super(gameContext);
        this.winner = winner;
        this.resultMessage = resultMessage;

        this.backButton = new Button(
                Viewport.WIDTH / 2f - 150,
                800,
                300,
                80,
                "BACK",
                ()  -> {
                    gameContext.getStateManager().setState(new MenuState(gameContext));
                }
        );
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {
        float gameMouseX = gameContext.getViewport().screenToGameX(app.mouseX);
        float gameMouseY = gameContext.getViewport().screenToGameY(app.mouseY);

        app.background(Colors.BACKGROUND);
        Typography.h1(app);
        app.text("GAME OVER", Viewport.WIDTH / 2f, 150);

        Typography.h2(app);
        app.text(resultMessage, Viewport.WIDTH / 2f, 250);

        if (winner != null) {
            PImage playerImage = gameContext.getAssetManager().loadImage(winner.getCharacter().getImageFileName());
            app.centeredImage(playerImage, Viewport.WIDTH / 2f, 525, 6f);
        }

        backButton.draw(app, gameMouseX, gameMouseY);

        Typography.hint(app);
        app.text("Press 'ENTER' or 'SPACE' to continue", Viewport.WIDTH / 2f, 920);
    }

    @Override
    public void mousePressed(int mouseX, int mouseY) {
        float gameX = gameContext.getViewport().screenToGameX(mouseX);
        float gameY = gameContext.getViewport().screenToGameY(mouseY);

        backButton.mousePressed(gameX, gameY);
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        switch (key){
            case ' ', '\n', '\r' -> gameContext.getStateManager().setState(new MenuState(gameContext));
        }
    }
}
