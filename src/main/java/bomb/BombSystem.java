package bomb;

import map.GameMap;
import map.Position;
import map.TileType;
import player.Player;
import powerup.PowerUpSystem;

import java.util.*;

public class BombSystem {

    private final static float FUSE_TIME = 2f;

    private final List<Bomb> bombs = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();

    public void placeBomb(Player player, GameMap map) {
        if (!player.canPlaceBomb()) {
            return;
        }

        Position position = map.worldToTile(
                player.getX(),
                player.getY()
        );

        if (findBomb(position) != null) {
            return;
        }

        bombs.add(new Bomb(
                position,
                player,
                player.getBombRange(),
                player.getBombBurst(),
                FUSE_TIME
        ));

        player.onBombPlaced();
    }

    public void update(float deltaTime, GameMap map, Player player, PowerUpSystem  powerUpSystem) {
        Queue<Bomb> pendingExplosions = new ArrayDeque<>();

        // Update Bombs
        for (Bomb bomb : bombs) {
            bomb.update(deltaTime);

            if (bomb.shouldExplode()) {
                pendingExplosions.add(bomb);
            }
        }

        // Add new Explosions
        while (!pendingExplosions.isEmpty()) {
            Bomb bomb  = pendingExplosions.remove();

            if (!bombs.remove(bomb)) {
                continue;
            }

            bomb.getOwner().onBombExploded();

            Set<Position> blast = createBlast(bomb, map, powerUpSystem);
            explosions.add(new Explosion(blast));

            for (Bomb otherBomb : new ArrayList<>(bombs)) {
                if (blast.contains(otherBomb.getPosition())) {
                    pendingExplosions.add(otherBomb);
                }
            }
        }

        // Update Explosions
        for (Explosion explosion : explosions) {
            explosion.update(deltaTime);
        }

        explosions.removeIf(Explosion::isFinished);

        // Kill Players
        Position playerPosition = map.worldToTile(
                player.getX(),
                player.getY());

        for (Explosion explosion : explosions) {
            if (explosion.contains(playerPosition)) {
                player.kill();
            }
        }
    }

    private Set<Position> createBlast(Bomb bomb, GameMap map, PowerUpSystem powerUpSystem) {
        Set<Position> blast = new HashSet<>();
        Position center = bomb.getPosition();

        blast.add(center);

        addDirection(blast, map, powerUpSystem, center, 1, 0, bomb.getRange(), bomb.getBurst());
        addDirection(blast, map, powerUpSystem, center, -1, 0, bomb.getRange(), bomb.getBurst());
        addDirection(blast, map, powerUpSystem, center, 0, 1, bomb.getRange(), bomb.getBurst());
        addDirection(blast, map, powerUpSystem, center, 0, -1, bomb.getRange(), bomb.getBurst());

        return blast;
    }

    private void addDirection(Set<Position> blast, GameMap map, PowerUpSystem powerUpSystem, Position center, int directionX, int directionY, int range, int burst) {
        int destroyedWalls = 0;

        for (int distance = 1; distance <= range; distance++) {
            int column = center.column() + directionX * distance;
            int row = center.row() + directionY * distance;

            if (!map.isInside(column, row)) {
                return;
            }

            TileType tile = map.getTile(column, row);

            if (tile == TileType.UNBREAKABLE_WALL) {
                return;
            }

            Position position = new Position(column, row);

            blast.add(position);

            if (map.isBreakable(column, row)) {
                map.destroyWall(column, row).ifPresent( type -> powerUpSystem.spawn(type, position));

                destroyedWalls++;

                if (destroyedWalls >= burst) {
                    return;
                }
            }
        }
    }

    private Bomb findBomb(Position position) {
        for (Bomb bomb : bombs) {
            if (bomb.getPosition().equals(position)) {
                return bomb;
            }
        }

        return null;
    }


    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }
}
