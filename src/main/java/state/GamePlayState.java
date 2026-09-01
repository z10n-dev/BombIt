package state;

import bomb.Bomb;
import bomb.BombSystem;
import bomb.Explosion;
import core.GameContext;
import game.*;
import highscore.HighScoreSaveResult;
import input.Controls;
import input.InputState;
import map.GameMap;
import map.Position;
import map.TileType;
import player.*;
import powerup.PowerUp;
import powerup.PowerUpSystem;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.sound.SoundFile;
import style.Colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePlayState extends GameState{

    private static final float BREAKABLE_RATIO = 0.8f;
    private static final float POWERUP_RATIO = 0.3f;

    private final GameConfig gameConfig;
    private final GameMap map;

    private final BombSystem bombSystem = new BombSystem();
    private final PowerUpSystem powerUpSystem = new PowerUpSystem();
    private final InputState inputState = new InputState();

    private final List<GamePlayer> gamePlayers = new ArrayList<>();

    private final GameWorld gameWorld;
    private final PImage[] explosionFrames;
    private final SoundFile explosionSound;

    private int lastUpdateTime;
    private final long matchStartNanos;
    private boolean gameEnded;

    private final PImage grassImage;
    private final PImage unbreakableWallImage;
    private final PImage breakableWallImage;
    private final PImage rangePowerUpWallImage;
    private final PImage speedPowerUpWallImage;
    private final PImage burstPowerUpWallImage;

    PGraphics mapLayer;
    int renderedMapRevision = -1;

    public GamePlayState(GameContext gameContext, GameConfig gameConfig) {
        super(gameContext);

        this.gameConfig = gameConfig;

        map = GameMap.loadMap("data/map.txt");

        map.generateRandomContent(
                new Random(),
                BREAKABLE_RATIO,
                POWERUP_RATIO
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

        explosionSound = gameContext.getAssetManager().loadSound("bomb_explosion.wav");
        explosionSound.amp(1f);

        grassImage = gameContext.getAssetManager().loadImage("grass.png");
        unbreakableWallImage = gameContext.getAssetManager().loadImage("unbreakable_wall.png");
        breakableWallImage = gameContext.getAssetManager().loadImage("breakable_wall.png");
        rangePowerUpWallImage = gameContext.getAssetManager().loadImage("range_power_up_wall.png");
        speedPowerUpWallImage = gameContext.getAssetManager().loadImage("speed_power_up_wall.png");
        burstPowerUpWallImage = gameContext.getAssetManager().loadImage("burst_power_up_wall.png");

        lastUpdateTime = app.millis();
        matchStartNanos = System.nanoTime();
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

        int startedExplosionCount = bombSystem.update(
                deltaTime,
                map,
                players,
                powerUpSystem
        );

        if (startedExplosionCount > 0) {
            explosionSound.play();
        }

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
        drawMap();
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

        gamePlayers.add(new GamePlayer(player, controller, playerConfig));
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

    private void checkGameOver(
            List<Player> players
    ) {
        List<Player> alivePlayers =
                players.stream()
                        .filter(Player::isAlive)
                        .toList();

        if (gameConfig.mode()
                == GameMode.SINGLE_PLAYER) {

            if (alivePlayers.isEmpty()) {
                finishGame(
                        GameOutcome.LOSS,
                        null,
                        false,
                        "GAME OVER!"
                );
            }

            return;
        }

        if (alivePlayers.size() > 1) {
            return;
        }

        if (alivePlayers.isEmpty()) {
            finishGame(
                    GameOutcome.DRAW,
                    null,
                    false,
                    "DRAW!"
            );

            return;
        }

        Player winner = alivePlayers.getFirst();

        Player defeatedPlayer =
                findDefeatedPlayer(
                        players,
                        winner
                );

        Player killer =
                bombSystem.getKiller(
                        defeatedPlayer
                );

        boolean winnerCausedElimination =
                killer == winner;

        PlayerConfig winnerConfig =
                findPlayerConfig(winner);

        finishGame(
                GameOutcome.WIN,
                winnerConfig,
                winnerCausedElimination,
                winnerConfig.playerName()
                        + " WINS!"
        );
    }

    private Player findDefeatedPlayer(
            List<Player> players,
            Player winner
    ) {
        for (Player player : players) {
            if (player != winner
                    && !player.isAlive()) {
                return player;
            }
        }

        throw new IllegalStateException(
                "No defeated player found"
        );
    }

    private PlayerConfig findPlayerConfig(Player player) {
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.player() == player) {
                return gamePlayer.config();
            }
        }

        throw new IllegalStateException(
                "Winner has no player configuration"
        );
    }

    private void finishGame(
            GameOutcome outcome,
            PlayerConfig winner,
            boolean winnerCausedElimination,
            String message
    ) {
        if (gameEnded) {
            return;
        }

        gameEnded = true;

        long durationMillis =
                (System.nanoTime()
                        - matchStartNanos)
                        / 1_000_000L;

        GameResult result =
                new GameResult(
                        gameConfig.mode(),
                        outcome,
                        winner,
                        winnerCausedElimination,
                        durationMillis,
                        message
                );

        HighScoreSaveResult saveResult =
                gameContext.getHighScoreService()
                        .record(result);

        gameContext.getStateManager().setState(
                new GameOverState(
                        gameContext,
                        result,
                        saveResult
                )
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

    private void drawMap() {

        initializeMapLayer();

        if (renderedMapRevision != map.getRevision()) {
            rebuildMapLayer();
        }

        app.image(mapLayer, map.getOffsetX(), map.getOffsetY());
    }

    private void initializeMapLayer() {
        if (mapLayer != null) {
            return;
        }

        int mapWidth = Math.round(
                map.getWidth() *  map.getTileSize()
        );

        int mapHeight = Math.round(
                map.getHeight() * map.getTileSize()
        );

        mapLayer = app.createGraphics(
                mapWidth,
                mapHeight,
                PConstants.P2D
        );
    }

    private void rebuildMapLayer() {
        float tileSize = map.getTileSize();

        mapLayer.beginDraw();
        mapLayer.clear();

        for (int row = 0; row < map.getHeight(); row ++) {
            for (int column = 0; column < map.getWidth(); column ++) {
                TileType tile = map.getTile(
                        column,
                        row
                );

                float x = column * tileSize;
                float y = row * tileSize;

                mapLayer.image(
                        grassImage,
                        x,
                        y,
                        tileSize,
                        tileSize
                );

                PImage wallImage = getWallImage(tile);

                if (wallImage != null) {
                    mapLayer.image(
                            wallImage,
                            x,
                            y,
                            tileSize,
                            tileSize
                    );
                }
            }
        }

        mapLayer.endDraw();
        renderedMapRevision = map.getRevision();
    }

    private PImage getWallImage(TileType tile) {
        return switch (tile) {
            case EMPTY -> null;
            case UNBREAKABLE_WALL -> unbreakableWallImage;
            case BREAKABLE_WALL -> breakableWallImage;
            case RANGE_POWER_UP_WALL -> rangePowerUpWallImage;
            case SPEED_POWER_UP_WALL -> speedPowerUpWallImage;
            case BURST_POWER_UP_WALL -> burstPowerUpWallImage;
        };
    }
}
