package state;

import core.GameContext;
import core.Viewport;
import processing.core.PApplet;
import style.Colors;
import ui.Button;
import ui.Typography;

import javax.swing.text.View;

public class MenuState extends GameState{

    private final Button playButton;
    private final Button highScoreButton;

    public MenuState(GameContext gameContext) {
        super(gameContext);
        playButton = new Button(
                Viewport.WIDTH / 2f - 320,
                650,
                300,
                80,
                "PLAY",
                this::selectGameMode
        );

        highScoreButton = new Button(
                Viewport.WIDTH / 2f + 20,
                650,
                300,
                80,
                "HIGHSCORES",
                this::showHighScores
        );
    }

    private void selectGameMode() {
        gameContext.getStateManager().setState(new GameModeSelectionState(gameContext));
    }

    private void showHighScores() {
        gameContext.getStateManager().setState(
                new HighScoreState(
                        gameContext,
                        this
                )
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

        gameContext.getTypography().h1();
        app.text("BOMB IT!", Viewport.WIDTH/2f, 150);

        gameContext.getTypography().h2();
        app.text("PLANT BOMBS, KILL ENEMIES, WIN THE GAME!", Viewport.WIDTH/2f, 250);

        app.centeredImage(gameContext.getAssetManager().loadImage("bomb.png"), Viewport.WIDTH/2f, 462, .5f);

        playButton.draw(app, gameMouseX, gameMouseY);
        highScoreButton.draw(
                app,
                gameMouseX,
                gameMouseY
        );

        gameContext.getTypography().hint();
        app.text("Press 'Space' or 'Return' to start the game!", Viewport.WIDTH/2f, 770);

        app.rectMode(PApplet.CENTER);
        app.fill(Colors.SECONDARY, 40f);
        app.stroke(Colors.SECONDARY);
        app.rect(Viewport.WIDTH/2f, 900, 700, 150, 12);
        app.line(Viewport.WIDTH / 2f, 835, Viewport.WIDTH / 2f, 965);

        gameContext.getTypography().hint();
        app.text("'W', 'A', 'S', 'D' to move!", Viewport.WIDTH/2f - 175, 950);
        app.text("'Space' to plant a bomb!", Viewport.WIDTH/2f + 175, 950);

        app.centeredImage(gameContext.getAssetManager().loadImage("expand-arrows.png"), Viewport.WIDTH/2f - 175, 880, .15f );
        app.centeredImage(gameContext.getAssetManager().loadImage("bomb_outline.png"), Viewport.WIDTH/2f + 175, 890, .15f );
    }

    @Override
    public void mousePressed(int mouseX, int mouseY) {
        float gameX = gameContext.getViewport().screenToGameX(mouseX);
        float gameY = gameContext.getViewport().screenToGameY(mouseY);

        playButton.mousePressed(gameX, gameY);
        highScoreButton.mousePressed(
                gameX,
                gameY
        );
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        if (key == 'h' || key == 'H') {
            showHighScores();
            return;
        }

        if (key == ' ' || key == '\n' || key == '\r') {
            selectGameMode();
        }
    }
}
