package player;

import map.Position;

import java.util.Set;

public record Hazard(
        Set<Position> positions,
        float startsAt,
        float endsAt
) {
}
