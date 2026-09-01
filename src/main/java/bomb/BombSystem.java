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
    private final Map<Player, Player> killersByVictim =
            new IdentityHashMap<>();

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

    public int update(float deltaTime, GameMap map, List<Player> players, PowerUpSystem  powerUpSystem) {
        int startedExplosionCount = 0;
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
            explosions.add(
                    new Explosion(
                            blast,
                            bomb.getOwner()
                    )
            );
            startedExplosionCount++;

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

        damagePlayers(map, players);

        return startedExplosionCount;
    }

    private void damagePlayers(
            GameMap map,
            List<Player> players
    ) {
        for (Player player : players) {
            if (!player.isAlive()) {
                continue;
            }

            Position playerPosition =
                    map.worldToTile(
                            player.getX(),
                            player.getY()
                    );

            Player killer = findKiller(
                    player,
                    playerPosition
            );

            if (killer == null) {
                continue;
            }

            killersByVictim.put(
                    player,
                    killer
            );

            player.kill();
        }
    }

    private Player findKiller(
            Player victim,
            Position victimPosition
    ) {
        Player selfKillSource = null;

        for (Explosion explosion : explosions) {
            if (!explosion.contains(
                    victimPosition
            )) {
                continue;
            }

            Player explosionOwner =
                    explosion.getOwner();

            if (explosionOwner != victim) {
                return explosionOwner;
            }

            selfKillSource = explosionOwner;
        }

        return selfKillSource;
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

    public Set<Position> previewBlast(Position center, int range, int burst, GameMap map) {
        Set<Position> blast = new HashSet<>();
        blast.add(center);

        addPreviewDirection(blast, center, 1, 0, range, burst, map);
        addPreviewDirection(blast, center, -1, 0, range, burst, map);
        addPreviewDirection(blast, center, 0, 1, range, burst, map);
        addPreviewDirection(blast, center, 0, -1, range, burst, map);

        return blast;
    }

    private void addPreviewDirection(Set<Position> blast, Position center, int directionX, int directionY, int range, int burst, GameMap map) {
        int destroyedWalls = 0;

        for (int distance = 1; distance <= range; distance ++) {
            int column = center.column() + directionX * distance;
            int row = center.row() + directionY * distance;

            if (!map.isInside(column, row)) {
                return;
            }

            TileType tile = map.getTile(column, row);

            if (tile == TileType.UNBREAKABLE_WALL) {
                return;
            }

            blast.add(new Position(column, row));

            if (map.isBreakable(column, row)) {
                destroyedWalls ++;

                if (destroyedWalls >= burst) {
                    return;
                }
            }
        }
    }


    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public float getFuseTime() {
        return FUSE_TIME;
    }

    public Player getKiller(Player victim) {
        return killersByVictim.get(victim);
    }
}
