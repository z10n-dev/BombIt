package player;

public record PlayerAction(
        float moveX,
        float moveY,
        boolean placeBomb
) {
    public static PlayerAction idle() {
        return new PlayerAction(0, 0, false );
    }
}
