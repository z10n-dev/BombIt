package highscore;

import game.GameMode;
import player.Character;

import java.time.Instant;
import java.util.Objects;

public record HighScoreEntry(
        String playerName, long durationMilis, GameMode mode, Character character, Instant achievedAt) {
  public HighScoreEntry {
    playerName = Objects.requireNonNull(playerName).trim();

    Objects.requireNonNull(mode);
    Objects.requireNonNull(character);
    Objects.requireNonNull(achievedAt);

    if (playerName.isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be empty");
    }

    if (durationMilis < 0) {
      throw new IllegalArgumentException("Duration must be non-negative");
    }

    if (mode == GameMode.SINGLE_PLAYER) {
      throw new IllegalArgumentException("High score entry cannot be for single player mode");
    }
  }
}
