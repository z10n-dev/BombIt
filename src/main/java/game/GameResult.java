package game;

import java.util.Objects;

public record GameResult(
        GameMode mode,
        GameOutcome outcome,
        PlayerConfig winner,
        boolean winnerCausedElimination,
        long durationMillis,
        String message
) {
  public GameResult {
    Objects.requireNonNull(mode);
    Objects.requireNonNull(outcome);
    Objects.requireNonNull(message);

    if (durationMillis < 0) {
      throw new IllegalArgumentException(
              "Duration must not be negative"
      );
    }

    if (outcome == GameOutcome.WIN
            && winner == null) {
      throw new IllegalArgumentException(
              "A win requires a winner"
      );
    }

    if (outcome != GameOutcome.WIN
            && winner != null) {
      throw new IllegalArgumentException(
              "Only a win may have a winner"
      );
    }

    if (outcome != GameOutcome.WIN
            && winnerCausedElimination) {
      throw new IllegalArgumentException(
              "Only a win may have an elimination"
      );
    }
  }
}