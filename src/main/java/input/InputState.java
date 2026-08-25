package input;

import java.util.HashSet;
import java.util.Set;

public class InputState {
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> justPressedKeys = new HashSet<>();

    public void keyPressed(int keyCode) {
        if (pressedKeys.add(keyCode)) {
            justPressedKeys.add(keyCode);
        }
    }

    public void keyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
    }

    public boolean isPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    public boolean wasJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }

    public void finishFrame() {
        justPressedKeys.clear();
    }
}
