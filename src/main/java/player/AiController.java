package player;

import game.GameWorld;

public class AiController implements PlayerController{
    @Override
    public PlayerAction update(Player player, GameWorld world, float deltaTime) {
        return PlayerAction.idle();
    }
}
