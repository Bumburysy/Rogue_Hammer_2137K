package project.roguelike.core;

import com.badlogic.gdx.math.Vector2;

public class InputState {
    private final Vector2 moveDirection;
    private final boolean shootPressed;
    private final boolean shootJustPressed;
    private final boolean reloadPressed;
    private final boolean usePressed;
    private final boolean useActiveItemPressed;
    private final boolean selectActiveItemPrevPressed;
    private final boolean selectActiveItemNextPressed;

    public InputState(Vector2 moveDirection,
            boolean shootPressed,
            boolean shootJustPressed,
            boolean reloadPressed,
            boolean usePressed,
            boolean useActiveItemPressed,
            boolean selectActiveItemPrevPressed,
            boolean selectActiveItemNextPressed) {
        this.moveDirection = new Vector2(moveDirection);
        this.shootPressed = shootPressed;
        this.shootJustPressed = shootJustPressed;
        this.reloadPressed = reloadPressed;
        this.usePressed = usePressed;
        this.useActiveItemPressed = useActiveItemPressed;
        this.selectActiveItemPrevPressed = selectActiveItemPrevPressed;
        this.selectActiveItemNextPressed = selectActiveItemNextPressed;
    }

    public Vector2 getMoveDirection() {
        return new Vector2(moveDirection);
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

    public boolean isUsePressed() {
        return usePressed;
    }

    public boolean isUseActiveItemPressed() {
        return useActiveItemPressed;
    }

    public boolean isSelectActiveItemPrevPressed() {
        return selectActiveItemPrevPressed;
    }

    public boolean isSelectActiveItemNextPressed() {
        return selectActiveItemNextPressed;
    }
}