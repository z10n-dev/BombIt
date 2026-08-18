package supermario;

import processing.core.PApplet;
import processing.core.PImage;

import static supermario.FileLoader.getImage;


public class Coin extends AnimatedSprite {
	public Coin(PApplet pApplet, PImage img, float scale) {
		super(pApplet, img, scale);
		standNeutral = new PImage[4];
		standNeutral[0] = getImage(pApplet, "gold1.png");
		standNeutral[1] = getImage(pApplet, "gold2.png");
		standNeutral[2] = getImage(pApplet, "gold3.png");
		standNeutral[3] = getImage(pApplet, "gold4.png");
		currentImages = standNeutral;
	}
}
