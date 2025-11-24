package project.roguelike.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class InputManager extends InputAdapter {
    private final KeyBindings keyBindings;
    private final Vector2 moveDirection = new Vector2();

    private boolean shootPressed = false;
    private boolean shootJustPressed = false;
    private boolean reloadPressed = false;
    private boolean usePressed = false;
    private boolean useActiveItemPressed = false;
    private boolean selectActiveItemPrevPressed = false;
    private boolean selectActiveItemNextPressed = false;

    private ScrollCallback scrollCallback;

    @FunctionalInterface
    public interface ScrollCallback {
        void onScroll(int amount);
    }

    public InputManager() {
        this.keyBindings = KeyBindings.getInstance();
    }

    public InputManager(KeyBindings customBindings) {
        this.keyBindings = customBindings;
    }

    public void setScrollCallback(ScrollCallback callback) {
        this.scrollCallback = callback;
    }

    public void update() {
        updateMovement();
        updateActions();
    }

    public InputState getState() {
        return new InputState(
                moveDirection,
                shootPressed,
                shootJustPressed,
                reloadPressed,
                usePressed,
                useActiveItemPressed,
                selectActiveItemPrevPressed,
                selectActiveItemNextPressed);
    }

    public boolean isActionJustPressed(InputAction action) {
        int key = keyBindings.getKey(action);
        return key != -1 && Gdx.input.isKeyJustPressed(key);
    }

    public KeyBindings getKeyBindings() {
        return keyBindings;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (scrollCallback != null) {
            scrollCallback.onScroll((int) amountY);
        }
        return true;
    }

    private void updateMovement() {
        moveDirection.setZero();

        if (isKeyPressed(InputAction.MOVE_UP))
            moveDirection.y += 1;
        if (isKeyPressed(InputAction.MOVE_DOWN))
            moveDirection.y -= 1;
        if (isKeyPressed(InputAction.MOVE_LEFT))
            moveDirection.x -= 1;
        if (isKeyPressed(InputAction.MOVE_RIGHT))
            moveDirection.x += 1;

        if (!moveDirection.isZero()) {
            moveDirection.nor();
        }
    }

    private void updateActions() {
        shootPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        shootJustPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        reloadPressed = isKeyJustPressed(InputAction.RELOAD);
        usePressed = isKeyJustPressed(InputAction.USE);
        useActiveItemPressed = isKeyJustPressed(InputAction.USE_ACTIVE_ITEM);
        selectActiveItemPrevPressed = isKeyJustPressed(InputAction.SELECT_ACTIVE_ITEM_PREV);
        selectActiveItemNextPressed = isKeyJustPressed(InputAction.SELECT_ACTIVE_ITEM_NEXT);
    }

    private boolean isKeyPressed(InputAction action) {
        int key = keyBindings.getKey(action);
        return key != -1 && Gdx.input.isKeyPressed(key);
    }

    private boolean isKeyJustPressed(InputAction action) {
        int key = keyBindings.getKey(action);
        return key != -1 && Gdx.input.isKeyJustPressed(key);
    }
}