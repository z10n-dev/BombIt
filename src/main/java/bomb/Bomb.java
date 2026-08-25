package bomb;

import map.Position;
import player.Player;

public class Bomb {

    private final Position position;
    private final Player owner;
    private final int range;
    private final int burst;
    private float remainingTime;

    public Bomb(
            Position position,
            Player owner,
            int range,
            int burst,
            float fuseTime
    ) {
        this.position = position;
        this.owner = owner;
        this.range = range;
        this.burst = burst;
        this.remainingTime = fuseTime;
    }

    public void update(float deltaTime) {
        remainingTime -= deltaTime;
    }

    public boolean shouldExplode() {
        return remainingTime <= 0;
    }

    public Position getPosition() {
        return position;
    }

    public Player getOwner() {
        return owner;
    }

    public int getRange() {
        return range;
    }

    public int getBurst() {
        return burst;
    }
}
