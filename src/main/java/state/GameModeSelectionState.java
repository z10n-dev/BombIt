package state;

import core.GameContext;
import core.Viewport;
import game.GameMode;
import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import style.Colors;
import ui.Button;
import ui.HoverCard;

public class GameModeSelectionState extends GameState {

  private static final float CARD_WIDTH = 420;
  private static final float CARD_HEIGHT = 400;
  private static final float CARD_GAP = 40;
  private static final float CARD_Y = 320;

  private final GameMode[] gameModes = GameMode.values();
  private final List<HoverCard> cards = new ArrayList<>();
  private final Button confirmButton;

  private int selectedIndex;

  public GameModeSelectionState(GameContext gameContext) {
    super(gameContext);

    float totalWidth = gameModes.length * CARD_WIDTH + (gameModes.length - 1) * CARD_GAP;

    float startX = (Viewport.WIDTH - totalWidth) / 2f;

    for (int i = 0; i < gameModes.length; i++) {
      int index = i;
      GameMode gameMode = gameModes[i];

      float cardX = startX + i * (CARD_WIDTH + CARD_GAP);
      float centerX = cardX + CARD_WIDTH / 2f;
      float centerY = CARD_Y + CARD_HEIGHT / 2f;

      cards.add(
          new HoverCard(
              cardX,
              CARD_Y,
              CARD_WIDTH,
              CARD_HEIGHT,
              () -> drawGameMode(gameMode, centerX, centerY),
              () -> select(index)));
    }

    confirmButton = new Button(Viewport.WIDTH / 2f - 350, 820, 700, 80, "", this::confirmSelection);
  }

  @Override
  public void update() {
    confirmButton.setLabel("CONTINUE WITH " + getTitle(gameModes[selectedIndex]));
  }

  @Override
  public void draw() {
    float mouseX = gameContext.getViewport().screenToGameX(app.mouseX);
    float mouseY = gameContext.getViewport().screenToGameY(app.mouseY);

    app.background(Colors.BACKGROUND);

    gameContext.getTypography().h3();
    app.text("SELECT GAME MODE", Viewport.WIDTH / 2f, 150);

    for (HoverCard card : cards) {
      card.draw(app, mouseX, mouseY);
    }

    confirmButton.draw(app, mouseX, mouseY);

    gameContext.getTypography().hint();
    app.text(
        "USE 'A' AND 'D' TO SELECT AND 'SPACE' OR 'RETURN' TO CONFIRM", Viewport.WIDTH / 2f, 950);
  }

  @Override
  public void keyPressed(char key, int keyCode) {
    boolean left = key == 'a' || key == 'A' || (key == PApplet.CODED && keyCode == PApplet.LEFT);

    boolean right = key == 'd' || key == 'D' || (key == PApplet.CODED && keyCode == PApplet.RIGHT);

    if (left) {
      select(selectedIndex - 1);
    } else if (right) {
      select(selectedIndex + 1);
    } else if (key == ' ' || key == '\n' || key == '\r') {
      confirmSelection();
    }
  }

  @Override
  public void mousePressed(int mouseX, int mouseY) {
    float gameMouseX = gameContext.getViewport().screenToGameX(mouseX);
    float gameMouseY = gameContext.getViewport().screenToGameY(mouseY);

    for (HoverCard card : cards) {
      card.mousePressed(gameMouseX, gameMouseY);
    }

    confirmButton.mousePressed(gameMouseX, gameMouseY);
  }

  private void select(int index) {
    selectedIndex = Math.floorMod(index, gameModes.length);

    for (int i = 0; i < cards.size(); i++) {
      cards.get(i).setSelected(i == selectedIndex);
    }
  }

  private void confirmSelection() {
    GameMode selectedMode = gameModes[selectedIndex];

    if (selectedMode == GameMode.SINGLE_PLAYER) {
      gameContext.getStateManager().setState(
          new CharacterSelectionState(gameContext, selectedMode, List.of("PLAYER 1")));
      return;
    }

    gameContext.getStateManager().setState(new PlayerNameEntryState(gameContext, selectedMode));
  }

  private void drawGameMode(GameMode gameMode, float centerX, float centerY) {
    app.pushStyle();

    app.textAlign(PApplet.CENTER, PApplet.CENTER);

    app.fill(Colors.PRIMARY);
    app.textSize(72);

    app.text(getIcon(gameMode), centerX, centerY - 90);

    gameContext.getTypography().cardTitle();

    app.text(getTitle(gameMode), centerX, centerY + 20);

    app.fill(Colors.TEXT);
    app.textSize(22);
    app.textLeading(30);

    app.text(getDescription(gameMode), centerX, centerY + 105);

    app.popStyle();
  }

  private String getIcon(GameMode gameMode) {
    return switch (gameMode) {
      case SINGLE_PLAYER -> "1P";
      case LOCAL_MULTIPLAYER -> "2P";
      case VS_AI -> "AI";
    };
  }

  private String getTitle(GameMode gameMode) {
    return switch (gameMode) {
      case SINGLE_PLAYER -> "SINGLE PLAYER";
      case LOCAL_MULTIPLAYER -> "LOCAL MULTIPLAYER";
      case VS_AI -> "VS AI";
    };
  }

  private String getDescription(GameMode gameMode) {
    return switch (gameMode) {
      case SINGLE_PLAYER -> "ONE PLAYER\nTRAINING MODE";

      case LOCAL_MULTIPLAYER -> "TWO PLAYERS\nONE KEYBOARD";

      case VS_AI -> "ONE PLAYER\nAGAINST AI";
    };
  }
}
