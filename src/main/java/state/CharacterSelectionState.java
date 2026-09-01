package state;

import core.GameContext;
import core.Viewport;
import game.ControllerType;
import game.GameConfig;
import game.GameMode;
import game.PlayerConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import player.Character;
import processing.core.PApplet;
import style.Colors;
import ui.Button;
import ui.HoverCard;

public class CharacterSelectionState extends GameState {

  private final Character[] characters = Character.values();
  private final List<HoverCard> cards = new ArrayList<>();
  private final List<Character> selections = new ArrayList<>();
  private final List<String> playerNames;

  private final GameMode gameMode;
  private final Button confirmButton;
  private final Random random = new Random();

  private int selectedIndex;

  public CharacterSelectionState(
      GameContext gameContext, GameMode gameMode, List<String> playerNames) {
    super(gameContext);
    this.gameMode = gameMode;
    this.playerNames = List.copyOf(playerNames);

    if (this.playerNames.size() != gameMode.getHumanPlayerCount()) {
      throw new IllegalArgumentException(
          "Player names count does not match game mode requirements.");
    }

    for (int i = 0; i < characters.length; i++) {
      int index = i;
      Character character = characters[i];

      float x = (i + 1) * (Viewport.WIDTH / (characters.length + 1f));
      float y = Viewport.HEIGHT / 2f;

      cards.add(
          new HoverCard(
              x - 100,
              y - 150,
              200,
              300,
              () -> drawCharacter(character, x, y),
              () -> select(index)));
    }

    confirmButton =
        new Button(
            Viewport.WIDTH / 2f - 325,
            850,
            650,
            80,
            "START GAME WITH " + characters[selectedIndex].getDisplayName().toUpperCase(),
            this::confirmSelection);

    select(0);
  }

  private void select(int index) {
    Character candidate = characters[index];

    if (selections.contains(candidate)) {
      return;
    }

    selectedIndex = index;

    for (int i = 0; i < cards.size(); i++) {
      cards.get(i).setSelected(i == selectedIndex);
    }
  }

  private void drawCharacter(Character character, float x, float y) {
    app.pushStyle();
    app.centeredImage(
        gameContext.getAssetManager().loadImage(character.getImageFileName()), x, y, 2f);
    gameContext.getTypography().cardTitle();
    app.text(character.getDisplayName(), x, y + 120);
    app.popStyle();
  }

  @Override
  public void update() {
    int playerNumber = selections.size() + 1;

    confirmButton.setLabel(
        "CONFIRM PLAYER "
            + playerNumber
            + ": "
            + characters[selectedIndex].getDisplayName().toUpperCase());
  }

  @Override
  public void draw() {
    app.background(Colors.BACKGROUND);

    gameContext.getTypography().h3();
    app.text(getHeading(), Viewport.WIDTH / 2f, 150);

    for (HoverCard card : cards) {
      card.draw(
          app,
          gameContext.getViewport().screenToGameX(app.mouseX),
          gameContext.getViewport().screenToGameY(app.mouseY));
    }

    confirmButton.draw(
        app,
        gameContext.getViewport().screenToGameX(app.mouseX),
        gameContext.getViewport().screenToGameY(app.mouseY));

    gameContext.getTypography().hint();
    app.text(
        "USE 'A' AND 'D' TO SELECT AND 'SPACE' OR 'RETURN' TO CONFIRM", Viewport.WIDTH / 2f, 950);
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

  @Override
  public void keyPressed(char key, int keyCode) {
    boolean left = key == 'a' || key == 'A' || (key == PApplet.CODED && keyCode == PApplet.LEFT);
    boolean right = key == 'd' || key == 'D' || (key == PApplet.CODED && keyCode == PApplet.RIGHT);

    if (left) {
      selectRelative(-1);
    } else if (right) {
      selectRelative(1);
    } else if (key == ' ' || key == '\n' || key == '\r') {
      confirmSelection();
    }
  }

  private String getHeading() {
    if (gameMode == GameMode.SINGLE_PLAYER || gameMode == GameMode.VS_AI) {
      return "SELECT YOUR CHARACTER";
    }

    return "PLAYER " + (selections.size() + 1) + ": SELECT YOUR CHARACTER";
  }

  private void selectRelative(int direction) {
    int candidateIndex = selectedIndex;

    for (int attempt = 0; attempt < characters.length; attempt++) {
      candidateIndex = Math.floorMod(candidateIndex + direction, characters.length);

      if (!selections.contains(characters[candidateIndex])) {
        select(candidateIndex);
        return;
      }
    }
  }

  private void confirmSelection() {
    Character selectedCharacter = characters[selectedIndex];

    if (selections.contains(selectedCharacter)) {
      return;
    }

    selections.add(selectedCharacter);

    if (selections.size() < gameMode.getHumanPlayerCount()) {
      selectRelative(1);
      return;
    }

    startGame();
  }

  private void startGame() {
    List<PlayerConfig> playerConfigs = new ArrayList<>();

    playerConfigs.add(
        new PlayerConfig(
            playerNames.get(0), selections.get(0), 1, ControllerType.HUMAN_PLAYER_ONE));

    if (gameMode == GameMode.LOCAL_MULTIPLAYER) {
      playerConfigs.add(
          new PlayerConfig(
              playerNames.get(1), selections.get(1), 2, ControllerType.HUMAN_PLAYER_TWO));
    }

    if (gameMode == GameMode.VS_AI) {
      playerConfigs.add(new PlayerConfig("AI", selectAiCharacter(), 2, ControllerType.AI));
    }

    GameConfig gameConfig = new GameConfig(gameMode, playerConfigs);

    gameContext.getStateManager().setState(new GamePlayState(gameContext, gameConfig));
  }

  private Character selectAiCharacter() {
    List<Character> availableCharacters =
        Arrays.stream(characters).filter(character -> !selections.contains(character)).toList();

    return availableCharacters.get(random.nextInt(availableCharacters.size()));
  }
}
