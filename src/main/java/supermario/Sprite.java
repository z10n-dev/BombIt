package supermario;

import processing.core.PApplet;
import processing.core.PImage;

public class Sprite {
	protected PApplet pApplet;
	protected PImage image;
	protected float center_x;
    protected float center_y;
	protected float change_x;
    protected float change_y;
	protected float w;
    protected float h;

	public Sprite(PApplet pApplet, PImage img, float scale) {
		this(pApplet, img, scale, 0, 0);
	}

	public Sprite(PApplet pApplet, PImage img, float scale, float x, float y) {
		this.pApplet = pApplet;
		image = img;
		w = image.width * scale;
		h = image.height * scale;
		center_x = x;
		center_y = y;
		change_x = 0;
		change_y = 0;
	}

	public void display() {
		pApplet.image(image, center_x, center_y, w, h);
	}

	public void update() {
		center_x += change_x;
		center_y += change_y;
	}

	void setLeft(float left) {
		center_x = left + w / 2;
	}

	float getLeft() {
		return center_x - w / 2;
	}

	void setRight(float right) {
		center_x = right - w / 2;
	}

	float getRight() {
		return center_x + w / 2;
	}

	void setTop(float top) {
		center_y = top + h / 2;
	}

	float getTop() {
		return center_y - h / 2;
	}

	void setBottom(float bottom) {
		center_y = bottom - h / 2;
	}

	float getBottom() {
		return center_y + h / 2;
	}
}
