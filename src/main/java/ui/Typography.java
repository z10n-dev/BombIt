package ui;

import processing.core.PApplet;
import processing.core.PFont;
import style.Colors;

public final class Typography {

    public static void h1(PApplet app) {
        PFont boldFont = app.createFont("SansSerif.bold", 128);

        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(boldFont);
        app.fill(Colors.PRIMARY);
    }

    public static void h2(PApplet app) {
        PFont boldFont = app.createFont("SansSerif.bold", 48);

        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(boldFont);
        app.fill(Colors.SECONDARY);
    }

    public static void h3(PApplet app) {
        PFont boldFont = app.createFont("SansSerif.bold", 92);

        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(boldFont);
        app.fill(Colors.PRIMARY);
    }

    public static void hint(PApplet app) {
        PFont font = app.createFont("SansSerif", 24);

        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(font);
        app.fill(Colors.SECONDARY);
    }

    public static void cardTitle(PApplet app) {
        PFont boldFont = app.createFont("SansSerif.bold", 28);

        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textFont(boldFont);
        app.fill(Colors.PRIMARY);
    }
}
