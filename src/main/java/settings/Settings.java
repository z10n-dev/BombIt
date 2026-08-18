package settings;

public final class Settings {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    public static final float SCALE_FACTOR = 1.0f;

    public static final int WINDOW_WIDTH = Math.round(GAME_WIDTH * SCALE_FACTOR);
    public static final int WINDOW_HEIGHT = Math.round(GAME_HEIGHT * SCALE_FACTOR);

    private Settings() {
    }

    public static int toGameCoordinate(int windowCoordinate) {
        return Math.round(windowCoordinate / SCALE_FACTOR);
    }
}
