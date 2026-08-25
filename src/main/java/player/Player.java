package player;

import map.GameMap;
import powerup.PowerUpType;

public class Player {

    private static final float INITIAL_SPEED = 220f;
    private static final int INITIAL_BURST = 1;
    private static final int INITIAL_RANGE = 2;
    private static final float HITBOX_SIZE = 42f;

    Character character;

    private float x;
    private float y;

    private int maximumBombs = 1;
    private int activeBombs;
    private boolean alive = true;
    private float speed = INITIAL_SPEED;
    private int burst =  INITIAL_BURST;
    private int range = INITIAL_RANGE;

    public Player(Character character, float x, float y) {
        this.character = character;
        this.x = x;
        this.y = y;
    }

    public void move(
            float directionX,
            float directionY,
            float deltaTime,
            GameMap map
    ) {
        float length = (float) Math.hypot(directionX, directionY);

        if (length == 0) {
            return;
        }

        // Verhindert schnellere diagonale Bewegung
        directionX /= length;
        directionY /= length;

        float movementX = directionX * speed * deltaTime;
        float movementY = directionY * speed * deltaTime;

        if (canStandAt(x + movementX, y, map)) {
            x += movementX;
        }

        if (canStandAt(x, y + movementY, map)) {
            y += movementY;
        }
    }

    private boolean canStandAt(float x, float y, GameMap map) {
        float halfSize = HITBOX_SIZE / 2f;

        int firstColumn = (int) Math.floor((x - halfSize - map.getOffsetX()) / map.getTileSize());
        int lastColumn = (int) Math.floor((x + halfSize -0.01f - map.getOffsetX()) / map.getTileSize());
        int firstRow = (int) Math.floor((y - halfSize - map.getOffsetY()) / map.getTileSize());
        int lastRow = (int) Math.floor((y + halfSize - 0.01f - map.getOffsetY()) / map.getTileSize());

        for (int row = firstRow; row <= lastRow; row++) {
            for (int column = firstColumn; column <= lastColumn; column++) {
                if (!map.isWalkable(column, row)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean canPlaceBomb() {
        return alive && activeBombs < maximumBombs;
    }

    public void onBombPlaced() {
        activeBombs ++;
    }

    public void onBombExploded() {
        activeBombs --;
    }

    public void kill() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public Character getCharacter() {
        return character;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSpeed() {
        return speed;
    }

    public int getBombBurst() {
        return burst;
    }

    public int getBombRange() {
        return range;
    }

    public void applyPowerUp(PowerUpType type) {
        switch (type) {
            case SPEED -> speed += 10f;
            case BURST -> burst ++;
            case RANGE -> range ++;
        }
    }
}
