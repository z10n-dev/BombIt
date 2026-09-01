package map;

import core.Viewport;
import powerup.PowerUpType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class GameMap {

    public static final float TILE_SIZE = 64;

    private final TileType[][] tiles;
    private final Map<Integer, Position> spawnPoints;
    private int revision;

    public GameMap(TileType[][] tiles, Map<Integer, Position> spawnPoints) {
        this.tiles = tiles;
        this.spawnPoints = spawnPoints;
        revision = 0;
    }

    public static GameMap loadMap(String mapPath) {
        InputStream input = GameMap.class.getClassLoader().getResourceAsStream(mapPath);

        if (input == null) {
            throw new IllegalArgumentException("Map file not found: " + mapPath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            List<String> lines = reader.lines().toList();
            int rows = lines.size();
            int columns = lines.get(0).length();

            TileType[][] tiles = new TileType[rows][columns];
            Map<Integer, Position> spawnPoints = new java.util.HashMap<>();

            for (int row = 0; row < rows; row++) {
                String line = lines.get(row);

                if (line.length() != columns) {
                    throw new IllegalArgumentException("Inconsistent row length in map file: " + mapPath);
                }

                for (int column = 0; column < columns; column++) {
                    char symbol = line.charAt(column);
                    TileType tileType = switch (symbol) {
                        case '.' -> TileType.EMPTY;
                        case '#' -> TileType.UNBREAKABLE_WALL;
                        case '1', '2', '3', '4' -> {
                            int playerNumber = Character.getNumericValue(symbol);
                            spawnPoints.put(playerNumber, new Position(column, row));
                            yield TileType.EMPTY;
                        }
                        default -> throw new IllegalArgumentException("Invalid tile character: " + symbol);
                    };
                    tiles[row][column] = tileType;
                }
            }

            return new GameMap(tiles, spawnPoints);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateRandomContent(Random random, float breakableRatio, float powerupRatio) {
        for (int row = 0; row < getHeight(); row++) {
            for (int column = 0; column < getWidth(); column++) {
                if (tiles[row][column] != TileType.EMPTY) {
                    continue;
                }

                if (isProtectedSpawnArea(column, row)) {
                    continue;
                }

                if (random.nextFloat() < breakableRatio) {
                    tiles[row][column] = TileType.BREAKABLE_WALL;

                    if (random.nextFloat() < powerupRatio) {
                        tiles[row][column] = randomPowerUp(random);
                    }
                }

            }
        }
    }

    private TileType randomPowerUp(Random random) {
        return switch (random.nextInt(3)) {
            case 0 -> TileType.SPEED_POWER_UP_WALL;
            case 1 -> TileType.BURST_POWER_UP_WALL;
            case 2 -> TileType.RANGE_POWER_UP_WALL;
            default -> TileType.EMPTY;
        };
    }

    private boolean isProtectedSpawnArea(int column, int row) {
        for (Position position : spawnPoints.values()) {
            int distance = Math.abs(column - position.column())
                    + Math.abs(row - position.row());

            if (distance <= 1) {
                return true;
            }
        }

        return false;
    }

    public int getWidth() {
        return tiles[0].length;
    }

    public int getHeight() {
        return tiles.length;
    }

    public TileType getTile(int column, int row) {
        return tiles[row][column];
    }

    public Position getSpawnPoint(int playerNumber) {
        Position position = spawnPoints.get(playerNumber);

        if (position == null) {
            throw new IllegalStateException("Player " + playerNumber + " has no spawn point!");
        }

        return position;
    }

    public boolean isWalkable(int column, int row) {
        if (column < 0 || column >= getWidth()
                || row < 0 || row >= getHeight()) {
            return false;
        }

        return getTile(column, row) == TileType.EMPTY;
    }

    public boolean isBreakable(int column, int row) {
        return switch (getTile(column, row)) {
            case BREAKABLE_WALL,
                 RANGE_POWER_UP_WALL,
                 BURST_POWER_UP_WALL,
                 SPEED_POWER_UP_WALL -> true;
            default -> false;
        };
    }

    public Optional<PowerUpType> destroyWall(int column, int row) {
        if (!isBreakable(column, row)) {
            return Optional.empty();
        }

        TileType tileType = getTile(column, row);


        tiles[row][column] = TileType.EMPTY;

        try {
            return switch (tileType) {
                case SPEED_POWER_UP_WALL -> Optional.of(PowerUpType.SPEED);
                case BURST_POWER_UP_WALL -> Optional.of(PowerUpType.BURST);
                case RANGE_POWER_UP_WALL -> Optional.of(PowerUpType.RANGE);
                default -> Optional.empty();
            };
        } finally {
            revision++;
        }
    }

    public Position worldToTile(float x, float y) {
        int column = (int) Math.floor((x - getOffsetX()) / TILE_SIZE);
        int row = (int) Math.floor((y - getOffsetY()) / TILE_SIZE);

        return new Position(column, row);
    }

    public boolean isInside(int column, int row) {
        return column >= 0
                && column < getWidth()
                && row >= 0
                && row < getHeight();
    }

    public float getTileSize() {
        return TILE_SIZE;
    }

    public float getOffsetX() {
        return (Viewport.WIDTH - getWidth() * TILE_SIZE) / 2;
    }

    public float getOffsetY() {
        return (Viewport.HEIGHT - getHeight() * TILE_SIZE) / 2;
    }

    public int getRevision() {
        return revision;
    }
}
