package bomb;

import map.Position;
import player.Player;

import java.util.Objects;
import java.util.Set;

public final class Explosion {

    private static final float DURATION = 0.5f;

    private final Set<Position> positions;
    private final Player owner;

    private float elapsedTime;

    public Explosion(
            Set<Position> positions,
            Player owner
    ) {
        this.positions = Set.copyOf(
                Objects.requireNonNull(positions)
        );

        this.owner =
                Objects.requireNonNull(owner);
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
    }

    public boolean isFinished() {
        return elapsedTime >= DURATION;
    }

    public boolean contains(Position position) {
        return positions.contains(position);
    }

    public Set<Position> getPositions() {
        return positions;
    }

    public Player getOwner() {
        return owner;
    }

    public int getFrameIndex(int frameCount) {
        float progress =
                elapsedTime / DURATION;

        return Math.min(
                frameCount - 1,
                (int) (progress * frameCount)
        );
    }

    public float getRemainingTime() {
        return Math.max(
                0,
                DURATION - elapsedTime
        );
    }

    public static float getDuration() {
        return DURATION;
    }
}