package ui;

import core.GameApplet;
import processing.core.PApplet;
import processing.core.PFont;
import style.Colors;

public final class Typography {

    private final GameApplet app;

    private PFont h1Font;
    private PFont h2Font;
    private PFont h3Font;
    private PFont hintFont;
    private PFont cardTitleFont;

    public Typography(GameApplet app) {
        this.app = app;
    }

    public void h1() {
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(h1Font);
        app.fill(Colors.PRIMARY);
    }

    public void h2() {
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(h2Font);
        app.fill(Colors.SECONDARY);
    }

    public void h3() {
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(h3Font);
        app.fill(Colors.PRIMARY);
    }

    public void hint() {
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(hintFont);
        app.fill(Colors.SECONDARY);
    }

    public void cardTitle() {
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(cardTitleFont);
        app.fill(Colors.PRIMARY);
    }

    public void setup() {
        h1Font = app.createFont("SansSerif.bold", 128);
        h2Font = app.createFont("SansSerif.bold", 48);
        h3Font = app.createFont("SansSerif.bold", 92);
        hintFont = app.createFont("SansSerif", 24);
        cardTitleFont = app.createFont("SansSerif.bold", 28);
    }
}
