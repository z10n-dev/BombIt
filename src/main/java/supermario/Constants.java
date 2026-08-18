package supermario;

public class Constants {
	final static float MOVE_SPEED = 4;
	final static float SPRITE_SCALE = (float) (50.0 / 128);
	final static float SPRITE_SIZE = 50;
	final static float HEIGHT = SPRITE_SIZE * 12;
	final static float GROUND_LEVEL = HEIGHT - SPRITE_SIZE;
	final static float WIDTH = SPRITE_SIZE * 16;
	final static float GRAVITY = .6F;
	final static float JUMP_SPEED = 14;
	final static int NEUTRAL_FACING = 0;
	final static int RIGHT_FACING = 1;
	final static int LEFT_FACING = 2;
	final static float RIGHT_MARGIN = 400;
	final static float LEFT_MARGIN = 60;
	final static float VERTICAL_MARGIN = 40;
}
