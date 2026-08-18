package demoapp;

import processing.core.PApplet;

/*Author: Michael Job*/
public class DemoApp extends PApplet {
    private float x = 100;

    @Override
    public void settings(){
        size(500,500);
    }

    @Override
    public void setup(){
        background(0);
        text("Hello Processing in IntelliJ IDEA", 90, 100);
    }

    @Override
    public void draw(){
        stroke(255,30,30);
        line(x, x*1.25F,x*3,x*1.25F);
        x++;
    }

    public static void main(String[] args) {
        PApplet.main(DemoApp.class, args);
    }

}
