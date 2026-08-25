package game;

public enum GameMode {
    SINGLE_PLAYER(1,1),
    LOCAL_MULTIPLAYER(2,2),
    VS_AI(2,1);

    private final int playerCount;
    private final int humanPlayerCount;

    GameMode(int playerCount, int humanPlayerCount) {
        this.playerCount = playerCount;
        this.humanPlayerCount = humanPlayerCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getHumanPlayerCount() {
        return humanPlayerCount;
    }
}
