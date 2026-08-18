package graph;

import processing.core.PApplet;

public class GraphApp extends PApplet {
    private Node origin;
    private Node actual;

    @Override
    public void settings() {
        size(640, 360);
    }

    @Override
    public void setup() {
        fill(0, 255, 0);
        reset();
    }

    @Override
    public void draw() {
        background(150);
        Node cur = origin;
        while (cur != null) {
            ellipse(cur.getX(), cur.getY(), 20, 20);
            Node next = cur.getNext();
            if (next != null) {
                fill(255, 0, 0);
                line(cur.getX(), cur.getY(), next.getX(), next.getY());
            }
            cur = next;
        }
    }

    @Override
    public void mousePressed() {
        Node n = new Node(mouseX, mouseY);
        actual.setNext(n);
        actual = n;
    }

    @Override
    public void keyPressed() {
        if (key == 'd') {
            reset();
        }
    }

    private void reset() {
        origin = new Node((int) random(0, width), (int) random(0, height));
        actual = origin;
    }

    public static void main(String[] args) {
        PApplet.main(GraphApp.class, args);
    }

}
