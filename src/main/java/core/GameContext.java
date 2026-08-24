package core;

import processing.core.PApplet;
import state.GameState;

public class GameContext {
    private final GameApplet app;
    private final StateManager stateManager;
    private final Viewport viewport;
    private final AssetManager assetManager;

    public GameContext(GameApplet app) {
        this.app = app;
        this.stateManager = new StateManager();
        this.viewport = new Viewport();
        this.assetManager = new AssetManager(app);
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
}
