package application;

import assets.AssetManager;
import processing.core.PApplet;
import processing.core.PImage;

public abstract class GameApp extends PApplet {
    private final AssetManager assetManager = new AssetManager(this);

    public PImage getAsset(String filename) {
        return assetManager.getImage(filename);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
