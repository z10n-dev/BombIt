package core;

import highscore.HighScoreService;
import processing.core.PApplet;
import state.GameState;
import ui.Typography;

public class GameContext {
    private final GameApplet app;
    private final StateManager stateManager;
    private final Viewport viewport;
    private final AssetManager assetManager;
    private final Typography typography;
    private final HighScoreService highScoreService;

    public GameContext(GameApplet app) {
        this.app = app;
        this.stateManager = new StateManager();
        this.viewport = new Viewport();
        this.assetManager = new AssetManager(app);
        this.typography = new Typography(app);
        this.highScoreService = new HighScoreService();
    }

    public GameApplet getApp() {
        return app;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Typography getTypography() {
        return typography;
    }

    public HighScoreService getHighScoreService() {
        return highScoreService;
    }
}
