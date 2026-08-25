package game;

import player.Character;

import java.util.Objects;

public record PlayerConfig(
        Character character,
        int spawnNumber,
        ControllerType controllerType
) {
    public PlayerConfig {
        Objects.requireNonNull(character);
        Objects.requireNonNull(controllerType);

        if (spawnNumber < 1) {
            throw new IllegalArgumentException(
                    "Spawn number must be positive"
            );
        }
    }
}
