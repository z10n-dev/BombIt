package powerup;

import map.GameMap;
import map.Position;
import player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PowerUpSystem {

    private static final float POWER_UP_LIFETIME = 10f;

    private final List<PowerUp>  powerUps = new ArrayList<>();

    public void spawn(PowerUpType type, Position position) {
        powerUps.add(
                new PowerUp(
                        type,
                        position,
                        POWER_UP_LIFETIME
                )
        );
    }

    public void update(float deltaTime, GameMap map, Player player) {
        Position playerPosition = map.worldToTile(
                player.getX(),
                player.getY()
        );

        Iterator<PowerUp> iterator = powerUps.iterator();

        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();

            powerUp.update(deltaTime);

            if (powerUp.shouldVanish()) {
                iterator.remove();
                continue;
            }

            if (powerUp.getPosition().equals(playerPosition)) {
                player.applyPowerUp(powerUp.getType());
                iterator.remove();
            }
        }
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
}
