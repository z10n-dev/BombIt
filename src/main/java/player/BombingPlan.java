package player;

import java.util.List;
import map.Position;

public record BombingPlan(
    Position position,
    List<Position> approachPath,
    List<Position> escapePath,
    int score,
    boolean attacksEnemy) {}
