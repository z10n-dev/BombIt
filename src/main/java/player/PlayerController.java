package player;

import game.GameWorld;

public interface PlayerController {
    PlayerAction update(
            Player player,
            GameWorld world,
            float deltaTime
    );
}
