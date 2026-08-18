package menu;

import application.GameApp;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import screen_framework.Screen;
import screen_framework.ScreenHandler;
import screen_framework.ScreenName;
import settings.Settings;

public class MenuScreen implements Screen {

    private static final float PLAY_BUTTON_X = Settings.GAME_WIDTH / 2.0f;
    private static final float PLAY_BUTTON_Y = 390;
    private static final float PLAY_BUTTON_WIDTH = 300;
    private static final float PLAY_BUTTON_HEIGHT = 68;

    private final ScreenHandler screenHandler;
    private final GameApp app;
    private final PFont headingFont;
    private final PFont buttonFont;
    private final PFont bodyFont;
    private final PImage bombImage;

    public MenuScreen(ScreenHandler screenHandler, GameApp app) {
        this.screenHandler = screenHandler;
        this.app = app;
        this.headingFont = app.createFont("SansSerif.bold", 58);
        this.buttonFont = app.createFont("SansSerif.bold", 20);
        this.bodyFont = app.createFont("SansSerif", 14);
        this.bombImage = app.getAsset("bomb.png");
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {
        app.pushStyle();
        drawBackground();
        drawTitle();
        drawBomb();
        drawPlayButton();
        drawControls();
        app.popStyle();
    }

    private void drawBackground() {
        int bottomColor = app.color(24, 38, 58);

        app.background(bottomColor);
    }

    private void drawTitle() {
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(headingFont);
        app.fill(0, 0, 0, 100);
        app.text("BOMB IT", Settings.GAME_WIDTH / 2.0f + 4, 101);
        app.fill(245, 248, 252);
        app.text("BOMB", Settings.GAME_WIDTH / 2.0f - 30, 97);
        app.fill(247, 168, 54);
        app.text("IT", Settings.GAME_WIDTH / 2.0f + 104, 97);
    }

    private void drawBomb() {
        float centerX = Settings.GAME_WIDTH / 2.0f;
        float centerY = 235;
        float imageSize = 190;

        app.imageMode(PApplet.CENTER);
        app.image(bombImage, centerX, centerY, imageSize, imageSize);
    }

    private void drawPlayButton() {

        boolean hovered = isInsidePlayButton(
                Settings.toGameCoordinate(app.mouseX),
                Settings.toGameCoordinate(app.mouseY)
        );

        float width = hovered ? PLAY_BUTTON_WIDTH + 12 : PLAY_BUTTON_WIDTH;
        float height = hovered ? PLAY_BUTTON_HEIGHT + 4 : PLAY_BUTTON_HEIGHT;

        app.rectMode(PApplet.CENTER);
        app.noStroke();
        app.fill(0, 0, 0, 80);
        app.rect(PLAY_BUTTON_X + 5, PLAY_BUTTON_Y + 7, width, height, 16);

        app.fill(hovered ? app.color(255, 184, 69) : app.color(247, 168, 54));
        app.rect(PLAY_BUTTON_X, PLAY_BUTTON_Y, width, height, 16);

        app.fill(20, 28, 42);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(buttonFont);
        app.text("SPIEL STARTEN", PLAY_BUTTON_X, PLAY_BUTTON_Y);
    }

    private void drawControls() {
        app.textFont(bodyFont);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textSize(13);
        app.fill(145, 159, 180);
        app.text("ENTER / LEERTASTE", Settings.GAME_WIDTH / 2.0f, 447);

        app.fill(255, 255, 255, 22);
        app.rectMode(PApplet.CENTER);
        app.rect(Settings.GAME_WIDTH / 2.0f, 525, 410, 64, 14);

        app.fill(205, 215, 230);
        app.textSize(13);
        app.text("BEWEGEN", 292, 514);
        app.text("BOMBE", 508, 514);
        app.textFont(buttonFont);
        app.textSize(13);
        app.fill(247, 168, 54);
        app.text("WASD / PFEILTASTEN", 292, 537);
        app.text("LEERTASTE", 508, 537);

    }

    @Override
    public void mousePressed(int x, int y) {
        if (isInsidePlayButton(x, y)) {
            startGame();
        }
    }

    @Override
    public void keyPressed(char key) {
        if (key == '\n' || key == '\r' || key == ' ') {
            startGame();
        }
    }

    private boolean isInsidePlayButton(int x, int y) {
        return x >= PLAY_BUTTON_X - PLAY_BUTTON_WIDTH / 2
                && x <= PLAY_BUTTON_X + PLAY_BUTTON_WIDTH / 2
                && y >= PLAY_BUTTON_Y - PLAY_BUTTON_HEIGHT / 2
                && y <= PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT / 2;
    }

    private void startGame() {
        screenHandler.setCurrentScreen(ScreenName.GAME);
    }
}
