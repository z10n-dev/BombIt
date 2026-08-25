package player;

import game.GameWorld;
import input.ControlScheme;
import input.InputState;

public class HumanController implements PlayerController{

    private final InputState input;
    private final ControlScheme controls;

    public HumanController(InputState input, ControlScheme controls){
        this.input = input;
        this.controls = controls;
    }

    @Override
    public PlayerAction update(Player player, GameWorld world, float deltaTime) {
        float moveX = pressed(controls.right()) - pressed(controls.left());
        float moveY = pressed(controls.down()) - pressed(controls.up());

        boolean placeBomb = input.wasJustPressed(controls.bomb());

        return new PlayerAction(
                moveX,
                moveY,
                placeBomb
        );

    }

    private int pressed(int keyCod){
        return input.isPressed(keyCod) ? 1 : 0;
    }
}
