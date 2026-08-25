package powerup;

public enum PowerUpType {
    SPEED("speed_power_up.png"),
    RANGE("range_power_up.png"),
    BURST("burst_power_up.png");

    private final String path;
    private PowerUpType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
