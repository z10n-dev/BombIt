package game;

import java.util.List;
import java.util.Objects;

public record GameConfig(
        GameMode mode,
        List<PlayerConfig> players
) {
    public GameConfig {
        Objects.requireNonNull(mode);
        Objects.requireNonNull(players);

        players = List.copyOf(players);

        if (players.size() != mode.getPlayerCount()) {
            throw new IllegalArgumentException(
                    "Game mode " + mode
                            + " requires "
                            + mode.getPlayerCount()
                            + " players"
            );
        }
    }
}
