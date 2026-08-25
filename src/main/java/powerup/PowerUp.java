package powerup;

import map.Position;

public class PowerUp {
    private final PowerUpType type;
    private final Position position;
    private float remainingTime;

    public PowerUp(
            PowerUpType type,
            Position position,
            float remainingTime
    ) {
        this.type = type;
        this.position = position;
        this.remainingTime = remainingTime;
    }

    public void update(float deltaTime) {
        remainingTime -= deltaTime;
    }

    public boolean shouldVanish() {
        return remainingTime <= 0;
    }

    public PowerUpType getType() {
        return type;
    }
    public Position getPosition() {
        return position;
    }


}
