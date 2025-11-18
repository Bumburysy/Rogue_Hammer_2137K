package project.roguelike.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class InputManager extends InputAdapter {
    private final WorldManager worldManager;
    private final Vector2 moveDirection = new Vector2();
    private boolean shootPressed = false;
    private boolean shootJustPressed = false;
    private boolean reloadPressed = false;
    private boolean pickupPressed = false;

    public InputManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public void update() {
        updateMovement();
        updateActions();
    }

    private void updateMovement() {
        moveDirection.setZero();

        if (Gdx.input.isKeyPressed(Input.Keys.W))
            moveDirection.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            moveDirection.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            moveDirection.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            moveDirection.x += 1;

        if (!moveDirection.isZero()) {
            moveDirection.nor();
        }
    }

    private void updateActions() {
        shootPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        shootJustPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        reloadPressed = Gdx.input.isKeyJustPressed(Input.Keys.R);
        pickupPressed = Gdx.input.isKeyJustPressed(Input.Keys.E);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        worldManager.handleScroll((int) amountY);
        return true;
    }

    public Vector2 getMoveDirection() {
        return moveDirection;
    }

    public boolean isShootPressed() {
        return shootPressed;
    }

    public boolean isShootJustPressed() {
        return shootJustPressed;
    }

    public boolean isReloadPressed() {
        return reloadPressed;
    }

    public boolean isPickupPressed() {
        return pickupPressed;
    }
}