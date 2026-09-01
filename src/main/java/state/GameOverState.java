package state;

import core.GameContext;
import core.Viewport;
import game.GameOutcome;
import game.GameResult;
import highscore.HighScoreSaveResult;
import highscore.HighScoreService;
import processing.core.PImage;
import style.Colors;
import ui.Button;

public class GameOverState extends GameState {

    private final GameResult result;
    private final HighScoreSaveResult saveResult;

    private final Button backButton;
    private final Button highScoreButton;

    public GameOverState(
            GameContext gameContext,
            GameResult result,
            HighScoreSaveResult saveResult
    ) {
        super(gameContext);

        this.result = result;
        this.saveResult = saveResult;

        backButton = new Button(
                Viewport.WIDTH / 2f - 320,
                820,
                300,
                80,
                "BACK",
                this::goBack
        );

        highScoreButton = new Button(
                Viewport.WIDTH / 2f + 20,
                820,
                300,
                80,
                "HIGHSCORES",
                this::showHighScores
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

        gameContext.getTypography().h1();

        app.text(
                "GAME OVER",
                Viewport.WIDTH / 2f,
                130
        );

        gameContext.getTypography().h2();

        app.text(
                result.message(),
                Viewport.WIDTH / 2f,
                250
        );

        if (result.winner() != null) {
            PImage playerImage =
                    gameContext.getAssetManager()
                            .loadImage(
                                    result.winner()
                                            .character()
                                            .getImageFileName()
                            );

            app.centeredImage(
                    playerImage,
                    Viewport.WIDTH / 2f,
                    500,
                    5f
            );
        }

        gameContext.getTypography().h2();

        app.text(
                "TIME: "
                        + HighScoreService.formatDuration(
                        result.durationMillis()
                ),
                Viewport.WIDTH / 2f,
                700
        );

        drawSaveResult();

        backButton.draw(
                app,
                mouseX,
                mouseY
        );

        highScoreButton.draw(
                app,
                mouseX,
                mouseY
        );

        gameContext.getTypography().hint();

        app.text(
                "PRESS H TO OPEN HIGHSCORES",
                Viewport.WIDTH / 2f,
                950
        );
    }

    private void drawSaveResult() {
        String message;

        if (saveResult
                == HighScoreSaveResult.SAVED) {
            message = "NEW PERSONAL BEST!";
        } else if (saveResult
                == HighScoreSaveResult.SAVED_FAILD) {
            message =
                    "HIGHSCORE COULD NOT BE SAVED";
        } else if (result.outcome()
                == GameOutcome.WIN
                && !result.winnerCausedElimination()) {
            message =
                    "NO HIGHSCORE: OPPONENT SELF-DESTRUCTED";
        } else {
            message = "";
        }

        if (message.isEmpty()) {
            return;
        }

        gameContext.getTypography().hint();

        app.text(
                message,
                Viewport.WIDTH / 2f,
                750
        );
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

        highScoreButton.mousePressed(
                gameX,
                gameY
        );
    }

    @Override
    public void keyPressed(
            char key,
            int keyCode
    ) {
        if (key == 'h' || key == 'H') {
            showHighScores();
            return;
        }

        if (key == ' '
                || key == '\n'
                || key == '\r') {
            goBack();
        }
    }

    private void goBack() {
        gameContext.getStateManager().setState(
                new MenuState(gameContext)
        );
    }

    private void showHighScores() {
        gameContext.getStateManager().setState(
                new HighScoreState(
                        gameContext,
                        this
                )
        );
    }
}