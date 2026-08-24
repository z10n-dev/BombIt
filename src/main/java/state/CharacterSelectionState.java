package state;

import core.GameContext;
import core.Viewport;
import player.Character;
import processing.core.PApplet;
import style.Colors;
import ui.Button;
import ui.HoverCard;
import ui.Typography;

import java.util.ArrayList;
import java.util.List;

public class CharacterSelectionState extends GameState{

    private final Character[] characters = Character.values();
    private final List<HoverCard> cards = new ArrayList<>();
    private int selectedIndex;

    public CharacterSelectionState(GameContext gameContext) {
        super(gameContext);

        for (int i = 0; i < characters.length; i++) {
            int index = i;
            Character character = characters[i];

            float x = (i + 1) * (Viewport.WIDTH / (characters.length + 1f));
            float y = Viewport.HEIGHT / 2f;

            cards.add(new HoverCard(
                    x - 100,
                    y - 150,
                    200,
                    300,
                    () -> drawCharacter(character, x, y),
                    () -> select(index)
            ));
        }

        select(0);
    }

    private void select(int index) {
        selectedIndex = Math.floorMod(index, cards.size());

        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setSelected(i == selectedIndex);
        }
    }

    private void drawCharacter(Character character, float x, float y) {
        app.pushStyle();
        app.centeredImage(gameContext.getAssetManager().loadImage(character.getImageFileName()), x, y, 2f);
        Typography.cardTitle(app);
        app.text(character.getDisplayName(), x, y + 120);
        app.popStyle();
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {
        app.background(Colors.BACKGROUND);

        Typography.h3(app);
        app.text("SELECT YOUR CHARACTER", Viewport.WIDTH / 2f, 150);

        for (HoverCard card : cards) {
            card.draw(app, gameContext.getViewport().screenToGameX(app.mouseX), gameContext.getViewport().screenToGameY(app.mouseY));
        }
        new Button(
                Viewport.WIDTH / 2f - 325,
                850,
                650,
                80,
                "START GAME WITH " + characters[selectedIndex].getDisplayName().toUpperCase(),
                () -> {
                    startGame(characters[selectedIndex]);
                }
        ).draw(app, gameContext.getViewport().screenToGameX(app.mouseX), gameContext.getViewport().screenToGameY(app.mouseY));
    }

    @Override
    public void mousePressed(int mouseX, int mouseY) {
        float gameMouseX = gameContext.getViewport().screenToGameX(mouseX);
        float gameMouseY = gameContext.getViewport().screenToGameY(mouseY);

        for (HoverCard card : cards) {
            card.mousePressed(gameMouseX, gameMouseY);
        }
    }

    @Override
    public void keyPressed(char key) {
        if (key == 'a' || key == 'A') {
            select(Math.floorMod(selectedIndex - 1, characters.length));
        } else if (key == 'd' || key == 'D') {
            select(Math.floorMod(selectedIndex + 1, characters.length));
        } else if (key == ' ' || key == '\n' || key == '\r') {
            startGame(characters[selectedIndex]);
        }
    }

    private void startGame(Character character) {
        gameContext.getStateManager().setState(new GamePlayState(gameContext, character));
    }
}
