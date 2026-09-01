package state;

import core.GameContext;
import core.Viewport;
import game.GameMode;
import game.PlayerConfig;
import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import style.Colors;
import ui.Button;

public class PlayerNameEntryState extends GameState {

  private final GameMode gameMode;
  private final int requiredNameCount;

  private final List<String> playerNames = new ArrayList<>();

  private final StringBuilder currentName = new StringBuilder();

  private final Button confirmButton;
  private final Button backButton;

  private String errorMessage = "";

  public PlayerNameEntryState(GameContext gameContext, GameMode gameMode) {
    super(gameContext);
    this.gameMode = gameMode;
    this.requiredNameCount = gameMode.getHumanPlayerCount();

    if (gameMode == GameMode.SINGLE_PLAYER) {
      throw new IllegalArgumentException("Single player mode does not require player name entry.");
    }

    confirmButton =
        new Button(Viewport.WIDTH / 2f - 320, 760, 300, 80, "CONFIRM", this::confirmCurrentName);

    backButton = new Button(Viewport.WIDTH / 2f + 20, 760, 300, 80, "BACK", this::goBack);
  }

  @Override
  public void update() {}

  @Override
  public void draw() {
    float mouseX = gameContext.getViewport().screenToGameX(app.mouseX);
    float mouseY = gameContext.getViewport().screenToGameY(app.mouseY);

    app.background(Colors.BACKGROUND);

    gameContext.getTypography().h3();
    app.text("ENTER PLAYER NAMES", Viewport.WIDTH / 2f, 140);

    drawInputField();
    drawConfirmedNames();

    if (!errorMessage.isEmpty()) {
      app.pushStyle();
      app.fill(Colors.PRIMARY);
      app.textAlign(PApplet.CENTER, PApplet.CENTER);
      app.textSize(24);
      app.text(errorMessage, Viewport.WIDTH / 2f, 680);
      app.popStyle();
    }

    confirmButton.draw(app, mouseX, mouseY);
    backButton.draw(app, mouseX, mouseY);

    gameContext.getTypography().hint();
    app.text("TYPE A NAME AND PRESS ENTER", Viewport.WIDTH / 2f, 930);
  }

  private void drawInputField() {
    app.pushStyle();

    app.rectMode(PApplet.CENTER);
    app.stroke(Colors.PRIMARY);
    app.strokeWeight(3);
    app.fill(Colors.TEXT, 20);

    app.rect(Viewport.WIDTH / 2f, 450, 700, 100, 12);

    app.fill(Colors.TEXT);
    app.textAlign(PApplet.CENTER, PApplet.CENTER);
    app.textSize(42);

    String displayedName = currentName.isEmpty() ? "_" : currentName + "_";
    app.text(displayedName, Viewport.WIDTH / 2f, 450);
    app.popStyle();
  }

  private void drawConfirmedNames() {
    if (playerNames.isEmpty()) {
      return;
    }

    app.pushStyle();
    app.fill(Colors.SECONDARY);
    app.textAlign(PApplet.CENTER, PApplet.CENTER);
    app.textSize(26);

    for (int index = 0; index < playerNames.size(); index++) {
      app.text(
          "PLAYER " + (index + 1) + ": " + playerNames.get(index),
          Viewport.WIDTH / 2f,
          560 + index * 45);
    }

    app.popStyle();
  }

  @Override
  public void keyTyped(char key) {
    if (java.lang.Character.isISOControl(key)) {
      return;
    }

    if (currentName.length() >= PlayerConfig.MAX_NAME_LENGTH) {
      return;
    }

    currentName.append(key);
    errorMessage = "";
  }

  @Override
  public void keyPressed(char key, int keyCode) {
    boolean backspace = key == PApplet.BACKSPACE || keyCode == PApplet.BACKSPACE;
    boolean confirm =
        key == '\n' || key == '\r' || keyCode == PApplet.ENTER || keyCode == PApplet.RETURN;

    if (backspace) {
      removeLastCharacter();
    }

    if (confirm) {
      confirmCurrentName();
    }
  }

  @Override
  public void mousePressed(int mouseX, int mouseY) {
    float gameMouseX = gameContext.getViewport().screenToGameX(mouseX);
    float gameMouseY = gameContext.getViewport().screenToGameY(mouseY);

    confirmButton.mousePressed(gameMouseX, gameMouseY);
    backButton.mousePressed(gameMouseX, gameMouseY);
  }

  private void removeLastCharacter() {
    if (currentName.isEmpty()) {
      return;
    }

    currentName.deleteCharAt(currentName.length() - 1);

    errorMessage = "";
  }

  private void confirmCurrentName() {
    String name = currentName.toString().trim();

    if (name.isEmpty()) {
      errorMessage = "Player name cannot be empty.";
      return;
    }

    if (containsName(name)) {
      errorMessage = "Player name must be unique.";
      return;
    }

    playerNames.add(name);
    currentName.setLength(0);
    errorMessage = "";

    if (playerNames.size() < requiredNameCount) {
      return;
    }

    gameContext
        .getStateManager()
        .setState(new CharacterSelectionState(gameContext, gameMode, playerNames));
  }

  private boolean containsName(String candidate) {
    String normalizedCandidate = candidate.trim().toLowerCase();

    for (String playerName : playerNames) {
      String normalizedPlayerName = playerName.trim().toLowerCase();

      if (normalizedPlayerName.equals(normalizedCandidate)) {
        return true;
      }
    }

    return false;
  }

  private void goBack() {
    gameContext.getStateManager().setState(new GameModeSelectionState(gameContext));
  }
}
