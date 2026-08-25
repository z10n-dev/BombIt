package game;

import bomb.BombSystem;
import bomb.Explosion;
import map.GameMap;
import map.Position;
import player.GamePlayer;
import player.Player;
import powerup.PowerUpSystem;

import java.util.List;

public class GameWorld {

    private final GameMap map;
    private final BombSystem bombSystem;
    private final PowerUpSystem powerUpSystem;
    private final List<GamePlayer> gamePlayers;

    public GameWorld(GameMap map, BombSystem bombSystem, PowerUpSystem powerUpSystem, List<GamePlayer> gamePlayers) {
        this.map = map;
        this.bombSystem = bombSystem;
        this.powerUpSystem = powerUpSystem;
        this.gamePlayers = gamePlayers;
    }

    public GameMap getMap() {
        return map;
    }

    public BombSystem getBombSystem() {
        return bombSystem;
    }

    public PowerUpSystem getPowerUpSystem() {
        return powerUpSystem;
    }

    public List<Player> getPlayers() {
        return gamePlayers.stream()
                .map(GamePlayer::player)
                .toList();
    }

    public List<Player> getEnemies(Player player) {
        return gamePlayers.stream()
                .map(GamePlayer::player)
                .filter(other -> other != player)
                .filter(Player::isAlive)
                .toList();
    }

    public boolean isDangerous(Position position) {
        for (Explosion explosion
                : bombSystem.getExplosions()) {
            if (explosion.contains(position)) {
                return true;
            }
        }
        return false;
    }
}
