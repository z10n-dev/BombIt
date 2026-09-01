package game;

import java.util.Objects;
import player.Character;

public record PlayerConfig(
    String playerName, Character character, int spawnNumber, ControllerType controllerType) {
  public static final int MAX_NAME_LENGTH = 16;

  public PlayerConfig {
    playerName = Objects.requireNonNull(playerName, "Player name is required").trim();

    Objects.requireNonNull(character);
    Objects.requireNonNull(controllerType);

    if (playerName.isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be empty");
    }

    if (playerName.length() > MAX_NAME_LENGTH) {
      throw new IllegalArgumentException(
          "Player name cannot exceed " + MAX_NAME_LENGTH + " characters");
    }

    for (int index = 0; index < playerName.length(); index++) {
      if (java.lang.Character.isISOControl(playerName.charAt(index))) {
        throw new IllegalArgumentException("Player name cannot contain control characters");
      }
    }

    if (spawnNumber < 1) {
      throw new IllegalArgumentException("Spawn number must be positive");
    }
  }
}
