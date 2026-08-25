package input;

import java.awt.event.KeyEvent;

public final class Controls {
    public static final ControlScheme PLAYER_ONE =  new ControlScheme(
            KeyEvent.VK_W,
            KeyEvent.VK_S,
            KeyEvent.VK_A,
            KeyEvent.VK_D,
            KeyEvent.VK_SPACE
    );

    public static final ControlScheme PLAYER_TWO =  new ControlScheme(
            KeyEvent.VK_UP,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT,
            KeyEvent.VK_ENTER
    );
}
