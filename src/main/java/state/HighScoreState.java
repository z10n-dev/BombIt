package state;

import core.GameContext;
import core.Viewport;
import game.GameMode;
import highscore.HighScoreEntry;
import highscore.HighScoreService;
import processing.core.PApplet;
import style.Colors;
import ui.Button;

import java.util.List;

public class HighScoreState extends GameState {

    private final GameState returnState;
    private final Button backButton;

    public HighScoreState(
            GameContext gameContext,
            GameState returnState
    ) {
        super(gameContext);

        this.returnState = returnState;

        backButton = new Button(
                Viewport.WIDTH / 2f - 150,
                900,
                300,
                80,
                "BACK",
                this::goBack
        );
    }

    @Override
    public void update() {
    }

    @Override
    public void draw() {
        float mouseX =
                gameContext.getViewport()
                        .screenToGameX(app.mouseX);

        float mouseY =
                gameContext.getViewport()
                        .screenToGameY(app.mouseY);

        app.background(Colors.BACKGROUND);

        gameContext.getTypography().h3();

        app.text(
                "HIGHSCORES",
                Viewport.WIDTH / 2f,
                120
        );

        List<HighScoreEntry> entries =
                gameContext.getHighScoreService()
                        .getEntries();

        if (entries.isEmpty()) {
            drawEmptyState();
        } else {
            drawHeader();
            drawEntries(entries);
        }

        backButton.draw(
                app,
                mouseX,
                mouseY
        );
    }

    private void drawEmptyState() {
        gameContext.getTypography().h2();

        app.text(
                "NO HIGHSCORES YET",
                Viewport.WIDTH / 2f,
                500
        );
    }

    private void drawHeader() {
        app.pushStyle();

        app.fill(Colors.PRIMARY);
        app.textAlign(
                PApplet.CENTER,
                PApplet.CENTER
        );
        app.textSize(28);

        app.text("RANK", 250, 245);
        app.text("PLAYER", 650, 245);
        app.text("TIME", 1120, 245);
        app.text("MODE", 1550, 245);

        app.stroke(Colors.SECONDARY);
        app.line(
                150,
                280,
                Viewport.WIDTH - 150,
                280
        );

        app.popStyle();
    }

    private void drawEntries(
            List<HighScoreEntry> entries
    ) {
        app.pushStyle();

        app.fill(Colors.TEXT);
        app.textAlign(
                PApplet.CENTER,
                PApplet.CENTER
        );
        app.textSize(28);

        for (int index = 0;
             index < entries.size();
             index++) {

            HighScoreEntry entry =
                    entries.get(index);

            float y = 325 + index * 52;

            app.text(
                    index + 1,
                    250,
                    y
            );

            app.text(
                    entry.playerName(),
                    650,
                    y
            );

            app.text(
                    HighScoreService.formatDuration(
                            entry.durationMilis()
                    ),
                    1120,
                    y
            );

            app.text(
                    formatMode(entry.mode()),
                    1550,
                    y
            );
        }

        app.popStyle();
    }

    private String formatMode(GameMode mode) {
        return switch (mode) {
            case LOCAL_MULTIPLAYER -> "LOCAL";
            case VS_AI -> "VS AI";
            case SINGLE_PLAYER -> "SINGLE";
        };
    }

    @Override
    public void mousePressed(
            int mouseX,
            int mouseY
    ) {
        float gameX =
                gameContext.getViewport()
                        .screenToGameX(mouseX);

        float gameY =
                gameContext.getViewport()
                        .screenToGameY(mouseY);

        backButton.mousePressed(
                gameX,
                gameY
        );
    }

    @Override
    public void keyPressed(
            char key,
            int keyCode
    ) {
        if (key == ' '
                || key == '\n'
                || key == '\r'
                || key == PApplet.BACKSPACE) {
            goBack();
        }
    }

    private void goBack() {
        gameContext.getStateManager().setState(
                returnState
        );
    }
}