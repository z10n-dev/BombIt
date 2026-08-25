package player;

import java.util.Objects;

public record GamePlayer(
        Player player,
        PlayerController controller
) {
    public GamePlayer {
        Objects.requireNonNull(player);
        Objects.requireNonNull(controller);
    }
}
