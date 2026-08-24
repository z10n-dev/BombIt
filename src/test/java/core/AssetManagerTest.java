package core;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AssetManagerTest {

    @Test
    void loadsAndCachesClasspathImage() {
        PApplet app = new PApplet();
        app.sketchPath();
        AssetManager assetManager = new AssetManager(app);

        PImage image = assetManager.loadImage("bomb_sprite.png");

        assertEquals(64, image.width);
        assertEquals(64, image.height);
        assertSame(image, assetManager.loadImage("bomb_sprite.png"));
    }
}
