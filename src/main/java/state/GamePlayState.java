package state;

import bomb.Bomb;
import bomb.BombSystem;
import bomb.Explosion;
import core.GameApplet;
import core.GameContext;
import core.Viewport;
import game.*;
import input.Controls;
import input.InputState;
import map.GameMap;
import map.Position;
import map.TileType;
import player.*;
import player.Character;
import powerup.PowerUp;
import powerup.PowerUpSystem;
import processing.core.PImage;
import style.Colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePlayState extends GameState{

    private final GameConfig gameConfig;
    private final GameMap map;

    private final BombSystem bombSystem = new BombSystem();
    private final PowerUpSystem powerUpSystem = new PowerUpSystem();
    private final InputState inputState = new InputState();

    private final List<GamePlayer> gamePlayers = new ArrayList<>();

    private final GameWorld gameWorld;
    private final PImage[] explosionFrames;

    private int lastUpdateTime;
    private boolean gameEnded;

    public GamePlayState(GameContext gameContext, GameConfig gameConfig) {
        super(gameContext);

        this.gameConfig = gameConfig;

        map = GameMap.loadMap("data/map.txt");

        map.generateRandomContent(
                new Random(),
                0.8f,
                0.3f
        );

        for (PlayerConfig playerConfig
                : gameConfig.players()) {
            createPlayer(playerConfig);
        }

        gameWorld = new GameWorld(
                map,
                bombSystem,
                powerUpSystem,
                gamePlayers
        );

        explosionFrames = new PImage[] {
                gameContext.getAssetManager().loadImage("bigboom1.png"),
                gameContext.getAssetManager().loadImage("bigboom2.png"),
                gameContext.getAssetManager().loadImage("bigboom3.png"),
                gameContext.getAssetManager().loadImage("bigboom4.png")
        };

        lastUpdateTime = app.millis();
    }

    @Override
    public void update() {
        if (gameEnded) {
            return;
        }

        int currentTime = app.millis();

        float deltaTime = Math.min((currentTime - lastUpdateTime) / 1000f, 0.05f);

        lastUpdateTime = currentTime;

        updatePlayers(deltaTime);

        List<Player> players = getPlayers();

        bombSystem.update(
                deltaTime,
                map,
                players,
                powerUpSystem
        );

        powerUpSystem.update(
                deltaTime,
                map,
                players
        );

        inputState.finishFrame();
        checkGameOver(players);
    }

    @Override
    public void draw() {
        app.background(Colors.BACKGROUND);
        drawMap(app);
        drawPowerUps();
        drawBombs();
        drawPlayers();
        drawExplosions();
    }

    @Override
    public void keyPressed(char key, int keyCode) {
        inputState.keyPressed(keyCode);
    }

    @Override
    public void keyReleased(char key, int keyCode) {
        inputState.keyReleased(keyCode);
    }

    private void createPlayer(PlayerConfig playerConfig) {
        Position spawn = map.getSpawnPoint(
                playerConfig.spawnNumber()
        );

        float x = map.getOffsetX() + (spawn.column() + 0.5f) * map.getTileSize();
        float y = map.getOffsetY() + (spawn.row() + 0.5f) * map.getTileSize();

        Player player = new Player(
                playerConfig.character(),
                x,
                y
        );

        PlayerController controller = createController(playerConfig.controllerType());

        gamePlayers.add(new GamePlayer(player, controller));
    }

    private PlayerController createController(ControllerType controllerType) {
        return switch (controllerType) {
            case HUMAN_PLAYER_ONE -> new HumanController(inputState, Controls.PLAYER_ONE);
            case HUMAN_PLAYER_TWO -> new HumanController(inputState, Controls.PLAYER_TWO);
            case AI -> new AiController();
        };
    }

    private void updatePlayers(float deltaTime) {
        for (GamePlayer gamePlayer : gamePlayers) {
            Player player = gamePlayer.player();

            if (!player.isAlive()) {
                continue;
            }

            PlayerAction action =
                    gamePlayer.controller().update(
                            player,
                            gameWorld,
                            deltaTime
                    );

            player.move(
                    action.moveX(),
                    action.moveY(),
                    deltaTime,
                    map
            );

            if (action.placeBomb()) {
                bombSystem.placeBomb(player, map);
            }
        }
    }

    private List<Player> getPlayers() {
        return gamePlayers.stream()
                .map(GamePlayer::player)
                .toList();
    }

    private void checkGameOver(List<Player> players) {
        List<Player> alivePlayers = players.stream().filter(Player::isAlive).toList();

        if (gameConfig.mode() == GameMode.SINGLE_PLAYER) {
            if (alivePlayers.isEmpty()) {
                finishGame(null, "GAME OVER!");
            }

            return;
        }

        if (alivePlayers.size() > 1) {
            return;
        }

        if (alivePlayers.isEmpty()) {
            finishGame(null, "DRAW!");
        }

        Player winner = alivePlayers.getFirst();
        finishGame(winner, winner.getCharacter().getDisplayName() + " WINS!");
    }

    private void finishGame(Player winner, String message) {
        gameEnded = true;

        gameContext.getStateManager().setState(
                new GameOverState(gameContext, winner, message)
        );
    }

    private void drawPlayers() {
        for (GamePlayer gamePlayer : gamePlayers) {
            Player player = gamePlayer.player();

            if (!player.isAlive()) {
                continue;
            }

            PImage image = gameContext.getAssetManager().loadImage(player.getCharacter().getImageFileName());

            app.centeredImage(
                    image,
                    player.getX(),
                    player.getY(),
                    1f
            );
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
