package ui;

import core.GameApplet;
import processing.core.PApplet;
import style.Colors;

public final class HoverCard {
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final Runnable content;
    private final Runnable onClick;
    private boolean selected = false;

    public HoverCard(float x, float y, float width, float height, Runnable content, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.content = content;
        this.onClick = onClick;
    }

    public void draw(GameApplet app, float mouseX, float mouseY) {
        boolean active = selected || contains(mouseX, mouseY);

        app.pushStyle();

        app.rectMode(PApplet.CORNER);
        app.noStroke();
        app.fill(Colors.SECONDARY, active ? 150 : 100);
        app.rect(x, y, width, height, 12);

        content.run();

        app.popStyle();
    }

    public boolean contains(float x, float y) {
        return x >= this.x
                && x <= this.x + width
                && y >= this.y
                && y <= this.y + height;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void mousePressed(float mouseX, float mouseY) {
        if (contains(mouseX, mouseY)) {
            onClick.run();
        }
    }
}
