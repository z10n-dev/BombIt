package state;

import core.GameContext;
import core.Viewport;
import processing.core.PApplet;
import style.Colors;
import ui.Button;
import ui.Typography;

public class MenuState extends GameState{

    private final Button playButton;

    public MenuState(GameContext gameContext) {
        super(gameContext);
        playButton = new Button(
                Viewport.WIDTH / 2f - 150,
                650,
                300,
                80,
                "PLAY",
                this::selectCharacter
        );
    }

    private void selectCharacter() {
        gameContext.getStateManager().setState(new CharacterSelectionState(gameContext));
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
        app.text("BOMB IT!", Viewport.WIDTH/2f, 150);

        Typography.h2(app);
        app.text("PLANT BOMBS, KILL ENEMIES, WIN THE GAME!", Viewport.WIDTH/2f, 250);

        app.centeredImage(gameContext.getAssetManager().loadImage("bomb.png"), Viewport.WIDTH/2f, 462, .5f);

        playButton.draw(app, gameMouseX, gameMouseY);

        Typography.hint(app);
        app.text("Press 'Space' or 'Return' to start the game!", Viewport.WIDTH/2f, 770);

        app.rectMode(PApplet.CENTER);
        app.fill(Colors.SECONDARY, 40f);
        app.stroke(Colors.SECONDARY);
        app.rect(Viewport.WIDTH/2f, 900, 700, 150, 12);
        app.line(Viewport.WIDTH / 2f, 835, Viewport.WIDTH / 2f, 965);

        Typography.hint(app);
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
    }

    @Override
    public void keyPressed(char key) {
        if (key == ' ' || key == '\n' || key == '\r') {
            selectCharacter();
        }
    }
}
