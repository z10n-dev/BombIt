package core;

import processing.core.PApplet;
import processing.core.PImage;

public class GameApplet extends PApplet {

    public void image(PImage img, float x, float y, float scale) {
        super.image(img, x, y, img.width * scale, img.height * scale);
    }

    public void centeredImage(PImage img, float x, float y, float scale) {
        super.image(img, x - (img.width * scale) / 2, y - (img.height * scale) / 2, img.width * scale, img.height * scale);
    }
}
