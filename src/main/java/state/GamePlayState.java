package state;

import bomb.Bomb;
import bomb.BombSystem;
import bomb.Explosion;
import core.GameApplet;
import core.GameContext;
import core.Viewport;
import map.GameMap;
import map.Position;
import map.TileType;
import player.Player;
import player.Character;
import powerup.PowerUp;
import powerup.PowerUpSystem;
import processing.core.PImage;
import style.Colors;

import java.util.Random;

public class GamePlayState extends GameState{


    private final Player player;
    private final GameMap map;

    private final BombSystem bombSystem = new BombSystem();
    private boolean bombKeyPressed;

    private final PowerUpSystem powerUpSystem = new PowerUpSystem();

    private final PImage[] explosionFrames;

    private boolean movingUp;
    private boolean movingDown;
    private boolean movingLeft;
    private boolean movingRight;

    private int lastUpdateTime;

    public GamePlayState(GameContext gameContext, Character selectedCharacter) {
        super(gameContext);

        map = GameMap.loadMap("data/map.txt");

        map.generateRandomContent(
                new Random(),
                0.8f,
                0.3f
        );

        Position spawn = map.getSpawnPoint(1);
        float spawnX = map.getOffsetX() + (spawn.column() + 0.5f) * map.getTileSize();
        float spawnY = map.getOffsetY() + (spawn.row() + 0.5f) * map.getTileSize();

        this.player = new Player(selectedCharacter,  spawnX, spawnY);

        lastUpdateTime = app.millis();

        explosionFrames = new PImage[] {
                gameContext.getAssetManager().loadImage("bigboom1.png"),
                gameContext.getAssetManager().loadImage("bigboom2.png"),
                gameContext.getAssetManager().loadImage("bigboom3.png"),
                gameContext.getAssetManager().loadImage("bigboom4.png")
        };
    }

    @Override
    public void update() {
        int currentTime = app.millis();

        float deltaTime = Math.min((currentTime - lastUpdateTime) / 1000f, 0.05f);

        lastUpdateTime = currentTime;

        float directionX = (movingRight ? 1 : 0) - (movingLeft ? 1 : 0);
        float directionY = (movingDown ? 1 : 0) - (movingUp ? 1 : 0);

        player.move(directionX, directionY, deltaTime, map);

        bombSystem.update(
                deltaTime,
                map,
                player,
                powerUpSystem
        );

        powerUpSystem.update(
                deltaTime,
                map,
                player
        );
    }

    @Override
    public void draw() {
        app.background(Colors.BACKGROUND);
        drawMap(app);
        drawPowerUps();
        drawBombs();
        drawExplosions();

        PImage playerImage = gameContext.getAssetManager().loadImage(player.getCharacter().getImageFileName());

        app.centeredImage(playerImage, player.getX(), player.getY(), 1);
    }

    @Override
    public void keyPressed(char key) {
        switch (java.lang.Character.toLowerCase(key)) {
            case 'w' -> movingUp = true;
            case 'a' -> movingLeft = true;
            case 's' ->  movingDown = true;
            case 'd' -> movingRight = true;
            case ' ' -> {
                if (!bombKeyPressed) {
                    bombSystem.placeBomb(player, map);
                    bombKeyPressed = true;
                }
            }
        }
    }

    @Override
    public void keyReleased(char key) {
        switch (java.lang.Character.toLowerCase(key)) {
            case 'w' -> movingUp = false;
            case 'a' -> movingLeft = false;
            case 's' -> movingDown = false;
            case 'd' -> movingRight = false;
            case ' ' -> bombKeyPressed = false;
        }
    }

    private void drawBombs() {
        PImage image = gameContext.getAssetManager().loadImage("bomb_sprite.png");

        for (Bomb bomb : bombSystem.getBombs()) {
            Position position = bomb.getPosition();

            float x = map.getOffsetX() + (position.column() + 0.5f) * map.getTileSize();
            float y = map.getOffsetY() + (position.row() + 0.5f) * map.getTileSize();

            app.centeredImage(image, x, y, 1f);
        }
    }

    private void drawExplosions() {
        for (Explosion explosion : bombSystem.getExplosions()) {
            int frameIndex = explosion.getFrameIndex(
                    explosionFrames.length
            );

            PImage frame = explosionFrames[frameIndex];

            for (Position position : explosion.getPositions()) {
                float x = map.getOffsetX() + (position.column() + 0.5f) * map.getTileSize();
                float y =  map.getOffsetY() + (position.row() + 0.5f) * map.getTileSize();

                app.centeredImage(frame, x, y, 1f);
            }
        }
    }

    private void drawPowerUps() {
        for (PowerUp powerUp : powerUpSystem.getPowerUps()) {
            Position position = powerUp.getPosition();

            float x = map.getOffsetX() + (position.column() + 0.5f) * map.getTileSize();
            float y = map.getOffsetY() + (position.row() + 0.5f) * map.getTileSize();

            PImage image = gameContext.getAssetManager().loadImage(powerUp.getType().getPath());

            app.centeredImage(image, x, y, 1f);
        }
    }

    private void drawMap(GameApplet app) {

        for (int row = 0; row < map.getHeight(); row ++) {
            for (int col = 0; col < map.getWidth(); col ++) {
                TileType tile = map.getTile(col, row);
                PImage tileSprite = switch (tile) {
                    case EMPTY -> gameContext.getAssetManager().loadImage("grass.png");
                    case UNBREAKABLE_WALL -> gameContext.getAssetManager().loadImage("unbreakable_wall.png");
                    case BREAKABLE_WALL -> gameContext.getAssetManager().loadImage("breakable_wall.png");
                    case RANGE_POWER_UP_WALL -> gameContext.getAssetManager().loadImage("range_power_up_wall.png");
                    case SPEED_POWER_UP_WALL -> gameContext.getAssetManager().loadImage("speed_power_up_wall.png");
                    case BURST_POWER_UP_WALL -> gameContext.getAssetManager().loadImage("burst_power_up_wall.png");
                };
                app.image(gameContext.getAssetManager().loadImage("grass.png"), map.getOffsetX() + col * map.getTileSize(),  map.getOffsetY() + row * map.getTileSize(), map.getTileSize(), map.getTileSize() );
                app.image(
                        tileSprite,
                        map.getOffsetX() + col * map.getTileSize(),
                        map.getOffsetY() + row * map.getTileSize(),
                        map.getTileSize(),
                        map.getTileSize()
                );
            }
        }
    }
}
