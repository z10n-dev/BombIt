package ui;

import core.GameApplet;
import processing.core.PApplet;
import style.Colors;

public final class Button {
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private String label;
    private final Runnable onClick;

    private boolean enabled = true;

    public Button(float x, float y, float width, float height, String label, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.onClick = onClick;
    }

    public void draw(GameApplet app, float mouseX, float mouseY) {
        boolean hovered = enabled && contains(mouseX, mouseY);

        app.pushStyle();

        app.rectMode(PApplet.CORNER);
        app.noStroke();
        app.fill(hovered ? Colors.PRIMARY_SHADE_1 : Colors.PRIMARY);
        app.rect(x, y, width, height, 12);

        app.fill(Colors.BACKGROUND);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textSize(32);
        app.text(label, x + width / 2, y + height / 2);

        app.popStyle();
    }

    public boolean mousePressed(float mouseX, float mouseY) {
        if (!enabled || !contains(mouseX, mouseY)) {
            return false;
        }

        onClick.run();
        return true;
    }

    public boolean contains(float pointX, float pointY) {
        return pointX >= x
                && pointX <= x + width
                && pointY >= y
                && pointY <= y + height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
