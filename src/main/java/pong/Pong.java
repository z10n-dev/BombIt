package pong;

import processing.core.PApplet;
import processing.sound.SoundFile;

import static pong.FileLoader.getSoundFile;

/*Source https://gist.github.com/dc74089/4094da7928839063ae06*/
public class Pong extends PApplet {
    public static final float BALL_DIAMETER = 20;
    private static final float PADDLE_HEIGHT = 80;

    private final int red = color(255, 0, 0);
    private final int green = color(0, 255, 0);

    private float x;
    private float y;
    private float speedX;
    private float speedY;

    private SoundFile bounce;
    private SoundFile frustrated;

    @Override
    public void settings() {
        size(640, 360);
        bounce = getSoundFile(this, "bounce.mp3");
        frustrated = getSoundFile(this, "frustrated.mp3");
    }

    @Override
    public void setup() {
        reset();
    }

    @Override
    public void draw() {
        background(0);

        drawBall();
        drawLeftPaddle();
        drawPaddle();

        x += speedX;
        y += speedY;

        // if ball hits movable bar, invert X direction
        if (x > width - 30 && x < width - 20 && y > mouseY - PADDLE_HEIGHT / 2 && y < mouseY + PADDLE_HEIGHT / 2) {
            speedX = speedX * -1;
            bounce.play();
        }

        // if ball hits wall, change direction of X
        if (x < 25) {
            speedX *= -1.1f;
            speedY *= 1.1f;
            x += speedX;
            bounce.play();
        }

        // if ball hits up or down, change direction of Y
        if (y > height || y < 0) {
            speedY *= -1;
            bounce.play();
        }

        if(x > width){
            lostTheBall();
        }
    }

    private void drawLeftPaddle() {
        fill(green);
        rect(0, 0, 20, height);
    }

    private void drawPaddle(){
        fill(green);
        rect(width - 30, mouseY - PADDLE_HEIGHT / 2, 10, PADDLE_HEIGHT);
    }

    private void drawBall() {
        fill(red);
        ellipse(x, y, BALL_DIAMETER, BALL_DIAMETER);
    }

    @Override
    public void mousePressed() {
        reset();
    }

    private void reset() {
        x = width / 2f;
        y = height / 2f;
        speedX = random(3, 5);
        speedY = random(3, 5);
    }

    private void lostTheBall() {
        speedX = 0;
        speedY = 0;
        x = width / 4f;
        y = height / 2f;
        frustrated.play();
    }

    public static void main(String[] args) {
        PApplet.main(Pong.class, args);
    }

}
