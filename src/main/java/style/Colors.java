package style;

public final class Colors {
    public static final int BACKGROUND = rgb(20, 33, 61);
//    public static final int BACKGROUND_SHADE_1 = rgb()
    public static final int TEXT = rgb(255, 255, 255);
    public static final int PRIMARY = rgb(252, 163, 17);
    public static final int PRIMARY_SHADE_1 = rgb(255, 181, 46);
    public static final int SECONDARY = rgb(229, 229, 229);

    private static int rgb(int r, int g, int b) {
        return 0xFF000000
                | (r << 16)
                | (g << 8)
                | b;
    }
}
