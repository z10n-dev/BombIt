package bouncingball;

import processing.core.PApplet;
import processing.core.PVector;
import processing.sound.SoundFile;

import static bouncingball.FileLoader.getSoundFile;

public class BouncingBall extends PApplet {
    /**
     * Bouncing Ball with Vectors
     * by Daniel Shiffman.
     * <p>
     * Demonstration of using vectors to control motion
     * of a body. This example is not object-oriented
     * See AccelerationWithVectors for an example of how
     * to simulate motion using vectors in an object.
     */

    private PVector location;  // Location of shape
    private PVector velocity;  // Velocity of shape
    private PVector gravity;   // Gravity acts at the shape's acceleration

    private SoundFile bounce;

    @Override
    public void settings() {
        size(640, 360);
        bounce = getSoundFile(this, "bounce.mp3");
    }

    @Override
    public void setup() {
        location = new PVector(100, 100);
        velocity = new PVector(1.5f, 2.1f);
        gravity = new PVector(0, 0.2f);
    }

    @Override
    public void draw() {
        background(0);

        // Add velocity to the location.
        location.add(velocity);

        // Add gravity to velocity
        velocity.add(gravity);

        // Bounce off edges
        if ((location.x > width) || (location.x < 0)) {
            velocity.x = velocity.x * -1;
            bounce.play();
        }
        if (location.y > height) {
            // We're reducing velocity ever so slightly
            // when it hits the bottom of the window
            velocity.y = velocity.y * -0.95f;
            location.y = height;
            bounce.play();
        }

        // Display circle at location vector
        stroke(255);
        strokeWeight(2);
        fill(127);
        ellipse(location.x, location.y, 48, 48);
    }

    public static void main(String[] args) {
        PApplet.main(BouncingBall.class, args);
    }
}
