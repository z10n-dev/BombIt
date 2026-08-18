package supermario;

import processing.core.PImage;

import static supermario.FileLoader.getImage;

public class Player extends AnimatedSprite {
	private final PImage[] standLeft;
	private final PImage[] standRight;
	private final PImage[] jumpLeft;
	private final PImage[] jumpRight;

	private int lives;
	private boolean onPlatform;
    private boolean inPlace;

	public Player(SuperMario platformer, String fileName, float scale) {
		super(platformer, getImage(platformer, fileName), scale);
		standLeft = new PImage[1];
		standLeft[0] = getImage(pApplet, "player_stand_left.png");
		standRight = new PImage[1];
		standRight[0] = getImage(pApplet, "player_stand_right.png");
		jumpLeft = new PImage[1];
		jumpLeft[0] = getImage(pApplet, "player_jump_left.png");
		jumpRight = new PImage[1];
		jumpRight[0] = getImage(pApplet, "player_jump_right.png");
		moveLeft = new PImage[2];
		moveLeft[0] = getImage(pApplet, "player_walk_left1.png");
		moveLeft[1] = getImage(pApplet, "player_walk_left2.png");
		moveRight = new PImage[2];
		moveRight[0] = getImage(pApplet, "player_walk_right1.png");
		moveRight[1] = getImage(pApplet, "player_walk_right2.png");

		currentImages = standRight;
		lives = 3;
		direction = Constants.RIGHT_FACING;
		onPlatform = false;
		inPlace = true;
	}

	public int getLives() {
		return lives;
	}

	public boolean isDead(){
		return lives <= 0;
	}

	public void decreaseLives() {
		lives--;
	}

	@Override
	public void updateAnimation() {
		onPlatform = ((SuperMario)pApplet).isOnPlatforms(this);
		inPlace = change_x == 0 && change_y == 0;
		super.updateAnimation();
	}

	@Override
	public void selectDirection() {
		if (change_x > 0)
			direction = Constants.RIGHT_FACING;
		else if (change_x < 0)
			direction = Constants.LEFT_FACING;
	}

	@Override
	public void selectCurrentImages() {
        switch (direction) {
            case Constants.RIGHT_FACING -> {
                if (inPlace) {
                    currentImages = standRight;
                } else if (!onPlatform)
                    currentImages = jumpRight;
                else
                    currentImages = moveRight;
            }
            case Constants.LEFT_FACING -> {
                if (inPlace)
                    currentImages = standLeft;
                else if (!onPlatform)
                    currentImages = jumpLeft;
                else
                    currentImages = moveLeft;
            }
        }
	}
}
