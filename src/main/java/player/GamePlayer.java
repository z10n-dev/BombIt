package player;

import game.PlayerConfig;
import java.util.Objects;

public record GamePlayer(Player player, PlayerController controller, PlayerConfig config) {
  public GamePlayer {
    Objects.requireNonNull(player);
    Objects.requireNonNull(controller);
  }
}
