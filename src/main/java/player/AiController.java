package player;

import bomb.Bomb;
import bomb.Explosion;
import game.GameWorld;
import java.util.*;
import map.GameMap;
import map.Position;
import powerup.PowerUp;

public class AiController implements PlayerController {

  private static final float REPLAN_INTERVAL = 0.25f;
  private static final float ESCAPE_MARGIN = 0.3f;

  private static final int[][] DIRECTIONS = {
    {1, 0},
    {-1, 0},
    {0, 1},
    {0, -1}
  };

  private final Deque<Position> path = new ArrayDeque<>();
  private boolean pathCalculated;
  private int plannedMapRevision = -1;
  private int plannedExplosionCount = -1;
  private int plannedBombCount = -1;

  private boolean escapingOwnBomb;

  private float replanTimer;

  private BombingPlan bombingPlan;

  @Override
  public PlayerAction update(Player player, GameWorld world, float deltaTime) {
    GameMap map = world.getMap();

    replanTimer -= deltaTime;

    int explosionCount = world.getBombSystem().getExplosions().size();
    int bombCount = world.getBombSystem().getBombs().size();

    Position currentPosition = map.worldToTile(player.getX(), player.getY());

    Set<Position> activeExplosionTiles = collectActiveExplosionTiles(world);

    Set<Position> futureBombTiles = collectFutureBombTiles(world, map);

    Set<Position> dangerTiles = new HashSet<>(activeExplosionTiles);

    // First Priority: Escape from danger
    if (escapingOwnBomb) {
      if (hasOwnBomb(player, world)) {
        return followPath(player, map, deltaTime);
      }

      escapingOwnBomb = false;
      path.clear();
      pathCalculated = false;
    }

    if (futureBombTiles.contains(currentPosition)) {
      List<Position> dangerEscapePath =
          findFutureDangerEscapePath(currentPosition, map, futureBombTiles, activeExplosionTiles);
      bombingPlan = null;
      path.clear();
      path.addAll(dangerEscapePath);
      pathCalculated = true;

      if (!dangerEscapePath.isEmpty()) {
        return followPath(player, map, deltaTime);
      }

      return PlayerAction.idle();
    }

    // Second Priority: Get PowerUps

    if (isStandingOnPowerUp(currentPosition, world)) {
      bombingPlan = null;
      return PlayerAction.idle();
    }

    List<Position> powerUpPath =
        findShortestPath(currentPosition, getPowerUpTargets(world), map, dangerTiles);

    if (!powerUpPath.isEmpty()) {
      bombingPlan = null;
      path.clear();
      path.addAll(powerUpPath);
      pathCalculated = true;

      return followPath(player, map, deltaTime);
    }

    if (bombingPlan != null && bombingPlan.position().equals(currentPosition)) {
      boolean safeToPlace = !dangerTiles.contains(currentPosition);

      List<Position> currentEscapePath =
          getEscapePathForBomb(currentPosition, player, world, map, dangerTiles);

      Set<Position> currentBlast =
          world
              .getBombSystem()
              .previewBlast(currentPosition, player.getBombRange(), player.getBombBurst(), map);

      boolean stillUseful =
          getWallScore(currentBlast, map) > 0
              || getEnemyHitCount(currentBlast, player, world, map) > 0;

      if (safeToPlace && player.canPlaceBomb() && stillUseful && !currentEscapePath.isEmpty()) {
        path.clear();
        path.addAll(currentEscapePath);

        escapingOwnBomb = true;
        pathCalculated = true;
        bombingPlan = null;

        return new PlayerAction(0, 0, true);
      }

      bombingPlan = null;
      path.clear();
      pathCalculated = false;
    }

    boolean timedReplanRequired = bombingPlan == null && replanTimer <= 0;

    if (!pathCalculated
        || timedReplanRequired
        || plannedMapRevision != map.getRevision()
        || plannedExplosionCount != explosionCount
        || plannedBombCount != bombCount
        || pathContainsDanger(dangerTiles)) {

      BombingPlan candidatePlan =
          findBestBombingPlan(player, world, map, currentPosition, dangerTiles);

      List<Position> newPath;

      if (candidatePlan != null && candidatePlan.attacksEnemy()) {
        bombingPlan = candidatePlan;
        newPath = candidatePlan.approachPath();
      } else {
        newPath = choosePath(player, world, map, currentPosition, dangerTiles);

        bombingPlan = null;

        if (newPath.isEmpty() && candidatePlan != null) {
          bombingPlan = candidatePlan;
          newPath = candidatePlan.approachPath();
        }
      }

      path.clear();
      path.addAll(newPath);

      pathCalculated = true;
      plannedMapRevision = map.getRevision();
      plannedExplosionCount = explosionCount;
      plannedBombCount = bombCount;
      replanTimer = REPLAN_INTERVAL;
    }

    return followPath(player, map, deltaTime);
  }

  private List<Position> findPath(
      Position start, Position target, GameMap map, Set<Position> dangerTiles) {
    if (!map.isInside(target.column(), target.row())) {
      return List.of();
    }

    if (!map.isWalkable(target.column(), target.row())) {
      return List.of();
    }

    Queue<Position> open = new ArrayDeque<>();
    Map<Position, Position> parent = new HashMap<>();

    open.add(start);
    parent.put(start, null);

    while (!open.isEmpty()) {
      Position current = open.remove();

      if (current.equals(target)) {
        return reconstructPath(start, target, parent);
      }

      for (int[] direction : DIRECTIONS) {
        Position next = new Position(current.column() + direction[0], current.row() + direction[1]);

        if (!map.isInside(next.column(), next.row())) {
          continue;
        }

        if (!map.isWalkable(next.column(), next.row())) {
          continue;
        }

        if (dangerTiles.contains(next)) {
          continue;
        }

        if (parent.containsKey(next)) {
          continue;
        }

        parent.put(next, current);
        open.add(next);
      }
    }

    return List.of();
  }

  private List<Position> reconstructPath(
      Position start, Position target, Map<Position, Position> parent) {
    LinkedList<Position> result = new LinkedList<>();

    Position current = target;

    while (!current.equals(start)) {
      result.addFirst(current);
      current = parent.get(current);
    }

    return result;
  }

  private PlayerAction followPath(Player player, GameMap map, float deltaTime) {
    if (path.isEmpty()) {
      return PlayerAction.idle();
    }

    Position currentPosition = map.worldToTile(player.getX(), player.getY());

    Position nextPosition = path.peekFirst();

    if (currentPosition.equals(nextPosition)) {
      if (path.size() == 1) {
        return moveToFinalCenter(player, map, nextPosition, deltaTime);
      }

      path.removeFirst();
      nextPosition = path.peekFirst();
    }

    int columnDistance = Math.abs(nextPosition.column() - currentPosition.column());

    int rowDistance = Math.abs(nextPosition.row() - currentPosition.row());

    if (columnDistance + rowDistance != 1) {
      path.clear();
      pathCalculated = false;

      return PlayerAction.idle();
    }

    float tolerance = Math.max(2f, player.getSpeed() * deltaTime);

    if (columnDistance == 1) {
      float currentCenterY = getTileCenterY(currentPosition, map);

      float verticalOffset = currentCenterY - player.getY();

      if (Math.abs(verticalOffset) > tolerance) {
        return new PlayerAction(0, Math.signum(verticalOffset), false);
      }

      return new PlayerAction(
          Math.signum(nextPosition.column() - currentPosition.column()), 0, false);
    }

    float currentCenterX = getTileCenterX(currentPosition, map);

    float horizontalOffset = currentCenterX - player.getX();

    if (Math.abs(horizontalOffset) > tolerance) {
      return new PlayerAction(Math.signum(horizontalOffset), 0, false);
    }

    return new PlayerAction(0, Math.signum(nextPosition.row() - currentPosition.row()), false);
  }

  private PlayerAction moveToFinalCenter(
      Player player, GameMap map, Position target, float deltaTime) {
    float targetX = getTileCenterX(target, map);
    float targetY = getTileCenterY(target, map);

    float distanceX = targetX - player.getX();
    float distanceY = targetY - player.getY();

    float tolerance = Math.max(2f, player.getSpeed() * deltaTime);

    if (Math.abs(distanceX) > tolerance) {
      return new PlayerAction(Math.signum(distanceX), 0, false);
    }

    if (Math.abs(distanceY) > tolerance) {
      return new PlayerAction(0, Math.signum(distanceY), false);
    }

    path.removeFirst();

    return PlayerAction.idle();
  }

  private float getTileCenterX(Position position, GameMap map) {
    return map.getOffsetX() + (position.column() + 0.5f) * map.getTileSize();
  }

  private float getTileCenterY(Position position, GameMap map) {
    return map.getOffsetY() + (position.row() + 0.5f) * map.getTileSize();
  }

  private boolean pathContainsDanger(Set<Position> dangerTiles) {
    for (Position position : path) {
      if (dangerTiles.contains(position)) {
        return true;
      }
    }
    return false;
  }

  private Set<Position> collectActiveExplosionTiles(GameWorld world) {
    Set<Position> positions = new HashSet<>();

    for (Explosion explosion : world.getBombSystem().getExplosions()) {
      positions.addAll(explosion.getPositions());
    }

    return positions;
  }

  private Set<Position> collectFutureBombTiles(GameWorld world, GameMap map) {
    Set<Position> positions = new HashSet<>();

    for (Bomb bomb : world.getBombSystem().getBombs()) {
      positions.addAll(
          world
              .getBombSystem()
              .previewBlast(bomb.getPosition(), bomb.getRange(), bomb.getBurst(), map));
    }

    return positions;
  }

  private List<Position> findShortestPath(
      Position start, List<Position> targets, GameMap map, Set<Position> dangerTiles) {
    List<Position> shortestPath = List.of();

    for (Position target : targets) {
      if (target.equals(start)) {
        continue;
      }

      List<Position> candidate = findPath(start, target, map, dangerTiles);

      if (candidate.isEmpty()) {
        continue;
      }

      if (shortestPath.isEmpty() || candidate.size() < shortestPath.size()) {
        shortestPath = candidate;
      }
    }

    return shortestPath;
  }

  private List<Position> getPowerUpTargets(GameWorld world) {
    List<Position> targets = new ArrayList<>();

    for (PowerUp powerUp : world.getPowerUpSystem().getPowerUps()) {
      targets.add(powerUp.getPosition());
    }

    return targets;
  }

  private List<Position> getEnemyTarget(Player player, GameWorld world, GameMap map) {
    List<Position> targets = new ArrayList<>();

    for (Player enemy : world.getEnemies(player)) {
      targets.add(map.worldToTile(enemy.getX(), enemy.getY()));
    }

    return targets;
  }

  private List<Position> choosePath(
      Player player,
      GameWorld gameWorld,
      GameMap map,
      Position currentPosition,
      Set<Position> dangerTiles) {
    return findShortestPath(
        currentPosition, getEnemyTarget(player, gameWorld, map), map, dangerTiles);
  }

  private List<Position> findFutureDangerEscapePath(
      Position start,
      GameMap map,
      Set<Position> futureBombTiles,
      Set<Position> activeExplosionTiles) {
    Queue<Position> open = new ArrayDeque<>();

    Map<Position, Position> parent = new HashMap<>();

    open.add(start);
    parent.put(start, null);

    while (!open.isEmpty()) {
      Position current = open.remove();

      boolean safeDestination =
          !current.equals(start)
              && !futureBombTiles.contains(current)
              && !activeExplosionTiles.contains(current);

      if (safeDestination) {
        return reconstructPath(start, current, parent);
      }

      for (int[] direction : DIRECTIONS) {
        Position next = new Position(current.column() + direction[0], current.row() + direction[1]);

        if (!map.isInside(next.column(), next.row())) {
          continue;
        }

        if (!map.isWalkable(next.column(), next.row())) {
          continue;
        }

        if (activeExplosionTiles.contains(next)) {
          continue;
        }

        if (parent.containsKey(next)) {
          continue;
        }

        parent.put(next, current);
        open.add(next);
      }
    }
    return List.of();
  }

  private List<Position> findEscapePath(
      Position start,
      Set<Position> ownBlast,
      Player player,
      GameWorld world,
      GameMap map,
      Set<Position> otherDangerTiles) {
    Queue<Position> open = new ArrayDeque<>();
    Map<Position, Position> parent = new HashMap<>();
    Map<Position, Integer> distance = new HashMap<>();

    float tileTravelTime = map.getTileSize() / player.getSpeed();

    int maximumSteps =
        (int) Math.floor((world.getBombSystem().getFuseTime() - ESCAPE_MARGIN) / tileTravelTime);
    open.add(start);
    parent.put(start, null);
    distance.put(start, 0);

    while (!open.isEmpty()) {
      Position current = open.remove();
      int currentDistance = distance.get(current);

      boolean safeDestination =
          !current.equals(start)
              && !ownBlast.contains(current)
              && !otherDangerTiles.contains(current);

      if (safeDestination) {
        return reconstructPath(start, current, parent);
      }

      if (currentDistance >= maximumSteps) {
        continue;
      }

      for (int[] direction : DIRECTIONS) {
        Position next = new Position(current.column() + direction[0], current.row() + direction[1]);

        if (!map.isInside(next.column(), next.row())) {
          continue;
        }

        if (!map.isWalkable(next.column(), next.row())) {
          continue;
        }

        if (otherDangerTiles.contains(next)) {
          continue;
        }

        if (parent.containsKey(next)) {
          continue;
        }

        parent.put(next, current);
        distance.put(next, currentDistance + 1);
        open.add(next);
      }
    }
    return List.of();
  }

  private List<Position> getEscapePathForBomb(
      Position bombPosition,
      Player player,
      GameWorld world,
      GameMap map,
      Set<Position> dangerTiles) {
    Set<Position> ownBlast =
        world
            .getBombSystem()
            .previewBlast(bombPosition, player.getBombRange(), player.getBombBurst(), map);

    return findEscapePath(bombPosition, ownBlast, player, world, map, dangerTiles);
  }

  private boolean hasOwnBomb(Player player, GameWorld world) {
    for (Bomb bomb : world.getBombSystem().getBombs()) {
      if (bomb.getOwner() == player) {
        return true;
      }
    }
    return false;
  }

  private boolean isStandingOnPowerUp(Position current, GameWorld world) {
    for (PowerUp powerUp : world.getPowerUpSystem().getPowerUps()) {
      if (powerUp.getPosition().equals(current)) {
        return true;
      }
    }
    return false;
  }

  private int getWallScore(Set<Position> blast, GameMap map) {
    int score = 0;

    for (Position position : blast) {
      score +=
          switch (map.getTile(position.column(), position.row())) {
            case BREAKABLE_WALL -> 100;
            case RANGE_POWER_UP_WALL, SPEED_POWER_UP_WALL, BURST_POWER_UP_WALL -> 250;
            default -> 0;
          };
    }

    return score;
  }

  private int getEnemyDistance(Position position, Player player, GameWorld world, GameMap map) {
    int shortestDistance = Integer.MAX_VALUE;

    for (Player enemy : world.getEnemies(player)) {
      Position enemyPosition = map.worldToTile(enemy.getX(), enemy.getY());

      int distance =
          Math.abs(position.column() - enemyPosition.column())
              + Math.abs(position.row() - enemyPosition.row());

      shortestDistance = Math.min(shortestDistance, distance);
    }

    if (shortestDistance == Integer.MAX_VALUE) {
      return 0;
    }

    return shortestDistance;
  }

  private int getEnemyHitCount(Set<Position> blast, Player player, GameWorld world, GameMap map) {
    int hitCount = 0;

    for (Player enemy : world.getEnemies(player)) {
      Position enemyPosition = map.worldToTile(enemy.getX(), enemy.getY());

      if (blast.contains(enemyPosition)) {
        hitCount++;
      }
    }

    return hitCount;
  }

  private BombingPlan findBestBombingPlan(
      Player player,
      GameWorld world,
      GameMap map,
      Position currentPosition,
      Set<Position> dangerTiles) {
    BombingPlan bestPlan = null;

    for (int row = 0; row < map.getHeight(); row++) {
      for (int column = 0; column < map.getWidth(); column++) {
        if (!map.isWalkable(column, row)) {
          continue;
        }

        Position position = new Position(column, row);

        if (dangerTiles.contains(position)) {
          continue;
        }

        List<Position> approachPath;

        if (position.equals(currentPosition)) {
          approachPath = List.of();
        } else {
          approachPath = findPath(currentPosition, position, map, dangerTiles);

          if (approachPath.isEmpty()) {
            continue;
          }
        }

        Set<Position> blast =
            world
                .getBombSystem()
                .previewBlast(position, player.getBombRange(), player.getBombBurst(), map);

        int wallScore = getWallScore(blast, map);

        int enemyHitCount = getEnemyHitCount(blast, player, world, map);

        if (wallScore <= 0 && enemyHitCount == 0) {
          continue;
        }

        List<Position> escapePath = getEscapePathForBomb(position, player, world, map, dangerTiles);

        if (escapePath.isEmpty()) {
          continue;
        }

        int enemyDistance = getEnemyDistance(position, player, world, map);

        int score = wallScore + enemyHitCount * 10_000;

        score -= approachPath.size() * 2;
        score -= escapePath.size();
        score -= enemyDistance * 3;

        BombingPlan candidate =
            new BombingPlan(position, approachPath, escapePath, score, enemyHitCount > 0);

        if (bestPlan == null || candidate.score() > bestPlan.score()) {
          bestPlan = candidate;
          continue;
        }

        if (candidate.score() == bestPlan.score()
            && candidate.approachPath().size() < bestPlan.approachPath().size()) {
          bestPlan = candidate;
        }
      }
    }

    return bestPlan;
  }
}
