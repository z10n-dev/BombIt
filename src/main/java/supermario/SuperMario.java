package supermario;

import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.SoundFile;

import java.util.ArrayList;
import java.util.List;

import static supermario.FileLoader.*;

public class SuperMario extends PApplet {

    private Player player;
    private PImage snow, crate, red_brick, brown_brick, gold, spider;
    private List<Sprite> platforms;
    private List<Sprite> coins;
    private Enemy enemy;

    private boolean isGameOver;
    private int numCoins;
    private float view_x = 0;
    private float view_y = 0;

    @Override
    public void settings() {
        size(800, 600);
        //fullScreen();
    }


    //initialize them in setup().
    public void setup() {
        //just to have an example for sound
        SoundFile sound = getSoundFile(this, "pole.mp3");
        sound.play();

        imageMode(CENTER);

        player = new Player(this, "player.png", 0.8f);
        player.center_x = 100;
        player.change_y = Constants.GROUND_LEVEL;

        platforms = new ArrayList<>();
        coins = new ArrayList<>();
        numCoins = 0;
        isGameOver = false;

        gold = getImage(this, "gold1.png");
        spider = getImage(this, "spider_walk_right1.png");
        red_brick = getImage(this, "red_brick.png");
        brown_brick = getImage(this, "brown_brick.png");
        crate = getImage(this, "crate.png");
        snow = getImage(this, "snow.png");

        createPlatforms();
    }

    // modify and update them in draw().
    public void draw() {
        background(255);

        scroll();
        displayAll();

        if (!isGameOver) {
            updateAll();
            collectCoins();
            checkDeath();
        }
    }

    private void updateAll() {
        player.updateAnimation();
        resolvePlatformCollisions(player, platforms);
        enemy.update();
        enemy.updateAnimation();
    }

    private void displayAll() {
        for (Sprite platform : platforms) {
            platform.display();
        }
        for (Sprite coin : coins) {
            coin.display();
            ((AnimatedSprite) coin).updateAnimation();
        }
        player.display();
        enemy.display();

        fill(255, 0, 0);
        textSize(32);
        text("Coin:" + numCoins, view_x + 50, view_y + 50);
        text("Lives:" + player.getLives(), view_x + 50, view_y + 100);

        if (isGameOver) {
            fill(0, 0, 255);
            text("GAME OVER!", (float) (view_x + width / 2.0 - 100), (float) (view_y + height / 2.0));
            if (player.isDead())
                text("You lose!", (float) (view_x + width / 2.0 - 100), (float) (view_y + height / 2.0 + 50));
            else
                text("You win!", (float) (view_x + width / 2.0 - 100), (float) (view_y + height / 2.0 + 50));
            text("Press SPACE to restart!", (float) (view_x + width / 2.0 - 100),
                    (float) (view_y + height / 2.0 + 100));

        }
    }

    private void checkDeath() {
        boolean hitEnemy = checkCollision(player, enemy);
        boolean fallOffCliff = player.getBottom() > Constants.GROUND_LEVEL;
        if (hitEnemy || fallOffCliff) {
            player.decreaseLives();
            if (player.isDead())
                isGameOver = true;
            else {
                player.center_x = 100;
                player.setBottom(Constants.GROUND_LEVEL);
            }
        }
    }

    void collectCoins() {
        ArrayList<Sprite> coin_list = checkCollisionList(player, coins);
        if (!coin_list.isEmpty()) {
            for (Sprite coin : coin_list) {
                numCoins++;
                coins.remove(coin);
            }
        }
        if (coins.isEmpty()) {
            isGameOver = true;
        }
    }

    void scroll() {
        float left_boundary = view_x + Constants.LEFT_MARGIN;
        if (player.getLeft() < left_boundary) {
            view_x -= left_boundary - player.getLeft();
        }
        float right_boundary = view_x + width - Constants.RIGHT_MARGIN;
        if (player.getRight() > right_boundary) {
            view_x += player.getRight() - right_boundary;
        }

        float top_boundary = view_y + Constants.VERTICAL_MARGIN;
        if (player.getTop() < top_boundary) {
            view_y -= top_boundary - player.getTop();
        }

        float bottom_boundary = view_y + height - Constants.VERTICAL_MARGIN;
        if (player.getBottom() > bottom_boundary) {
            view_y += player.getBottom() - bottom_boundary;
        }
        translate(-view_x, -view_y);
    }

    public boolean isOnPlatforms(Sprite s) {
        s.center_y += 5;
        ArrayList<Sprite> col_list = checkCollisionList(s, platforms);
        s.center_y -= 5;
        return !col_list.isEmpty();
    }

    public void resolvePlatformCollisions(Sprite s, List<Sprite> walls) {
        s.change_y += Constants.GRAVITY;
        s.center_y += s.change_y;
        ArrayList<Sprite> col_list = checkCollisionList(s, walls);
        if (!col_list.isEmpty()) {
            Sprite collided = col_list.getFirst();
            if (s.change_y > 0) {
                s.setBottom(collided.getTop());
            } else if (s.change_y < 0) {
                s.setTop(collided.getBottom());
            }
            s.change_y = 0;
        }

        s.center_x += s.change_x;
        col_list = checkCollisionList(s, walls);
        if (!col_list.isEmpty()) {
            Sprite collided = col_list.getFirst();
            if (s.change_x > 0) {
                s.setRight(collided.getLeft());
            } else if (s.change_x < 0) {
                s.setLeft(collided.getRight());
            }
        }
    }

    boolean checkCollision(Sprite s1, Sprite s2) {
        boolean noXOverlap = s1.getRight() <= s2.getLeft() || s1.getLeft() >= s2.getRight();
        boolean noYOverlap = s1.getBottom() <= s2.getTop() || s1.getTop() >= s2.getBottom();
        return !noXOverlap && !noYOverlap;
    }

    public ArrayList<Sprite> checkCollisionList(Sprite s, List<Sprite> list) {
        ArrayList<Sprite> collision_list = new ArrayList<>();
        for (Sprite p : list) {
            if (checkCollision(s, p))
                collision_list.add(p);
        }
        return collision_list;
    }

    void createPlatforms() {
        String[] lines = getData(this, "map.csv");
        for (int row = 0; row < lines.length; row++) {
            String[] values = split(lines[row], ",");
            for (int col = 0; col < values.length; col++) {
                switch (values[col]) {
                    case "1" -> {
                        Sprite s = new Sprite(this, red_brick, Constants.SPRITE_SCALE);
                        s.center_x = Constants.SPRITE_SIZE / 2 + col * Constants.SPRITE_SIZE;
                        s.center_y = Constants.SPRITE_SIZE / 2 + row * Constants.SPRITE_SIZE;
                        platforms.add(s);
                    }
                    case "2" -> {
                        Sprite s = new Sprite(this, snow, Constants.SPRITE_SCALE);
                        s.center_x = Constants.SPRITE_SIZE / 2 + col * Constants.SPRITE_SIZE;
                        s.center_y = Constants.SPRITE_SIZE / 2 + row * Constants.SPRITE_SIZE;
                        platforms.add(s);
                    }
                    case "3" -> {
                        Sprite s = new Sprite(this, brown_brick, Constants.SPRITE_SCALE);
                        s.center_x = Constants.SPRITE_SIZE / 2 + col * Constants.SPRITE_SIZE;
                        s.center_y = Constants.SPRITE_SIZE / 2 + row * Constants.SPRITE_SIZE;
                        platforms.add(s);
                    }
                    case "4" -> {
                        Sprite s = new Sprite(this, crate, Constants.SPRITE_SCALE);
                        s.center_x = Constants.SPRITE_SIZE / 2 + col * Constants.SPRITE_SIZE;
                        s.center_y = Constants.SPRITE_SIZE / 2 + row * Constants.SPRITE_SIZE;
                        platforms.add(s);
                    }
                    case "5" -> {
                        Coin coin = new Coin(this, gold, Constants.SPRITE_SCALE);
                        coin.center_x = Constants.SPRITE_SIZE / 2 + col * Constants.SPRITE_SIZE;
                        coin.center_y = Constants.SPRITE_SIZE / 2 + row * Constants.SPRITE_SIZE;
                        coins.add(coin);
                    }
                    case "6" -> {
                        float bLeft = col * Constants.SPRITE_SIZE;
                        float bRight = bLeft + 5 * Constants.SPRITE_SIZE;
                        enemy = new Enemy(this, spider, 50.0f / 72.0f, bLeft, bRight);
                        enemy.center_x = Constants.SPRITE_SIZE / 2 + col * Constants.SPRITE_SIZE;
                        enemy.center_y = Constants.SPRITE_SIZE / 2 + row * Constants.SPRITE_SIZE;
                    }
                }
            }
        }
    }

    // called whenever a key is pressed.
    public void keyPressed() {
        if (isGameOver && key == ' ') {
            setup();
        } else if (keyCode == RIGHT || key == 'd') {
            player.change_x = Constants.MOVE_SPEED;
        } else if (keyCode == LEFT || key == 'a') {
            player.change_x = -Constants.MOVE_SPEED;
        } else if ((keyCode == UP || key == ' ') && isOnPlatforms(player)) {
            player.change_y = -Constants.JUMP_SPEED;
        }
    }

    // called whenever a key is released.
    public void keyReleased() {
        if (keyCode == RIGHT || key == 'd') {
            player.change_x = 0;
        } else if (keyCode == LEFT || key == 'a') {
            player.change_x = 0;
        }
    }

    public static void main(String[] args) {
        PApplet.main(SuperMario.class, args);
    }
}
