package bomb;

import map.Position;

import java.util.Set;

public final class Explosion {
    private static final float DURATION = .5f;

    private final Set<Position> positions;
    private float elapsedTime;

    public Explosion(Set<Position> positions) {
        this.positions = positions;
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

    public int getFrameIndex(int frameCount) {
        float progress = elapsedTime / DURATION;

        return Math.min(
                frameCount - 1,
                (int) (progress * frameCount)
        );
    }
}
