package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;
import project.roguelike.core.InputAction;
import project.roguelike.core.KeyBindings;

public class ControlsScene implements Scene {
    private final SceneManager sceneManager;
    private KeyBindings keyBindings;
    private final Vector3 mousePosition = new Vector3();
    private final InputAction[] remappableActions;
    private final Rectangle[] buttonBounds;

    private Viewport viewport;
    private Texture titleTexture;
    private Texture resetTexture;
    private Texture backTexture;
    private BitmapFont font;
    private Rectangle resetBounds;
    private Rectangle backBounds;

    private InputAction waitingForKey;
    private int hoveredIndex = -1;
    private boolean resetHovered;
    private boolean backHovered;

    public ControlsScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.remappableActions = getRemappableActions();
        this.buttonBounds = new Rectangle[remappableActions.length];
    }

    @Override
    public void create() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        viewport.apply(true);

        keyBindings = KeyBindings.getInstance();

        loadTextures();
        initializeFont();
        initializeBounds();

        Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
    }

    @Override
    public void update(float delta) {
        updateMousePosition();
        updateHoveredStates();
        handleInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        renderTitle(batch);
        renderControlsList(batch);
        renderResetButton(batch);
        renderBackButton(batch);

        if (waitingForKey != null) {
            renderWaitingPrompt(batch);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (titleTexture != null) {
            titleTexture.dispose();
        }
        resetTexture.dispose();
        backTexture.dispose();
        font.dispose();
    }

    private InputAction[] getRemappableActions() {
        InputAction[] allActions = InputAction.values();
        int remappableCount = 0;

        for (InputAction action : allActions) {
            if (isRemappable(action)) {
                remappableCount++;
            }
        }

        InputAction[] remappable = new InputAction[remappableCount];
        int index = 0;

        for (InputAction action : allActions) {
            if (isRemappable(action)) {
                remappable[index++] = action;
            }
        }

        return remappable;
    }

    private boolean isRemappable(InputAction action) {
        return action != InputAction.SHOOT && action != InputAction.WEAPON_SCROLL;
    }

    private void loadTextures() {
        try {
            titleTexture = new Texture("ui/controls.png");
        } catch (Exception e) {
            titleTexture = null;
        }
        resetTexture = new Texture("ui/reset.png");
        backTexture = new Texture("ui/back.png");
    }

    private void initializeFont() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(GameConfig.UI_TEXT_SCALE_SMALL);
    }

    private void initializeBounds() {
        float centerX = GameConfig.WORLD_WIDTH / 2f;

        float y = GameConfig.WORLD_HEIGHT - GameConfig.UI_TITLE_MARGIN_TOP;
        y -= GameConfig.UI_TITLE_HEIGHT;
        y -= GameConfig.UI_TITLE_MARGIN_BOTTOM / 2f;

        float buttonX = centerX + GameConfig.UI_CONTROLS_BUTTON_X_OFFSET;

        for (int i = 0; i < remappableActions.length; i++) {
            buttonBounds[i] = new Rectangle(
                    buttonX,
                    y,
                    200f,
                    GameConfig.UI_ELEMENT_HEIGHT);
            y -= GameConfig.UI_ELEMENT_SPACING_COMPACT;
        }

        y -= GameConfig.UI_ELEMENT_SPACING_COMPACT / 2f;
        resetBounds = createCenteredButtonBounds(resetTexture, centerX, y);

        backBounds = createCenteredButtonBounds(backTexture, centerX, GameConfig.UI_MARGIN_BOTTOM);
    }

    private Rectangle createCenteredButtonBounds(Texture texture, float centerX, float y) {
        float aspect = (float) texture.getWidth() / texture.getHeight();
        float height = GameConfig.UI_ELEMENT_HEIGHT;
        float width = height * aspect;
        return new Rectangle(centerX - width / 2f, y, width, height);
    }

    private void updateMousePosition() {
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePosition);
    }

    private void updateHoveredStates() {
        hoveredIndex = -1;

        if (waitingForKey == null) {
            for (int i = 0; i < buttonBounds.length; i++) {
                if (buttonBounds[i].contains(mousePosition.x, mousePosition.y)) {
                    hoveredIndex = i;
                    break;
                }
            }
        }

        resetHovered = resetBounds.contains(mousePosition.x, mousePosition.y);
        backHovered = backBounds.contains(mousePosition.x, mousePosition.y);
    }

    private void handleInput() {
        if (waitingForKey != null) {
            handleKeyRemap();
            return;
        }

        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            return;
        }

        if (backHovered) {
            sceneManager.popScene();
        } else if (resetHovered) {
            handleResetClick();
        } else if (hoveredIndex != -1) {
            waitingForKey = remappableActions[hoveredIndex];
        }
    }

    private void handleKeyRemap() {
        for (int keycode = 0; keycode < GameConfig.UI_MAX_KEYCODE; keycode++) {
            if (Gdx.input.isKeyJustPressed(keycode)) {
                handleKeyPress(keycode);
                return;
            }
        }
    }

    private void handleKeyPress(int keycode) {
        if (waitingForKey == null)
            return;

        if (keycode == Input.Keys.ESCAPE) {
            waitingForKey = null;
            return;
        }

        keyBindings.setKey(waitingForKey, keycode);
        waitingForKey = null;
    }

    private void handleResetClick() {
        keyBindings.resetToDefaults();
    }

    private void renderTitle(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float aspect = (float) titleTexture.getWidth() / titleTexture.getHeight();
        float width = GameConfig.UI_TITLE_HEIGHT * aspect;
        float x = centerX - width / 2f;
        float y = GameConfig.WORLD_HEIGHT - GameConfig.UI_TITLE_HEIGHT - GameConfig.UI_TITLE_MARGIN_TOP;

        batch.draw(titleTexture, x, y, width, GameConfig.UI_TITLE_HEIGHT);
    }

    private void renderControlsList(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float labelX = centerX - GameConfig.UI_CONTROLS_LABEL_X_OFFSET;

        for (int i = 0; i < remappableActions.length; i++) {
            InputAction action = remappableActions[i];
            Rectangle bounds = buttonBounds[i];
            float centerY = bounds.y + bounds.height / 2f;
            boolean hovered = hoveredIndex == i;

            renderActionLabel(batch, action, labelX, centerY);
            renderKeyButton(batch, action, bounds, centerY, hovered);
        }
    }

    private void renderActionLabel(SpriteBatch batch, InputAction action, float x, float centerY) {
        font.setColor(Color.WHITE);
        font.draw(batch, keyBindings.getActionDisplayName(action), x, centerY);
    }

    private void renderKeyButton(SpriteBatch batch, InputAction action, Rectangle bounds, float centerY,
            boolean hovered) {
        if (waitingForKey == action) {
            renderWaitingState(batch, bounds, centerY);
        } else {
            renderNormalKeyState(batch, action, bounds, centerY, hovered);
        }

        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
    }

    private void renderWaitingState(SpriteBatch batch, Rectangle bounds, float centerY) {
        font.setColor(Color.YELLOW);
        font.draw(batch, "Press key...", bounds.x + GameConfig.UI_CONTROLS_KEY_X_OFFSET, centerY);
    }

    private void renderNormalKeyState(SpriteBatch batch, InputAction action, Rectangle bounds, float centerY,
            boolean hovered) {
        float scale = hovered ? GameConfig.UI_HOVER_SCALE : 1.0f;

        applyButtonTint(batch, hovered);
        font.getData().setScale(GameConfig.UI_TEXT_SCALE_SMALL * scale);

        String keyName = keyBindings.getKeyName(action);
        font.draw(batch, keyName, bounds.x + GameConfig.UI_CONTROLS_KEY_X_OFFSET, centerY);

        font.getData().setScale(GameConfig.UI_TEXT_SCALE_SMALL);
    }

    private void renderWaitingPrompt(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float centerY = GameConfig.WORLD_HEIGHT / 2f;

        font.getData().setScale(GameConfig.UI_VALUE_TEXT_SCALE);
        font.setColor(Color.YELLOW);

        String text = "Press any key for " + keyBindings.getActionDisplayName(waitingForKey);

        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        layout.setText(font, text);
        float textX = centerX - layout.width / 2f;

        font.draw(batch, text, textX, centerY + 20f);

        font.getData().setScale(GameConfig.UI_TEXT_SCALE_SMALL);
        font.setColor(Color.GRAY);

        String cancelText = "(ESC to cancel)";
        layout.setText(font, cancelText);
        float cancelX = centerX - layout.width / 2f;

        font.draw(batch, cancelText, cancelX, centerY - 20f);

        font.setColor(Color.WHITE);
        font.getData().setScale(GameConfig.UI_TEXT_SCALE_SMALL);
    }

    private void renderResetButton(SpriteBatch batch) {
        renderCenteredButton(batch, resetTexture, resetBounds, resetHovered);
    }

    private void renderBackButton(SpriteBatch batch) {
        renderCenteredButton(batch, backTexture, backBounds, backHovered);
    }

    private void renderCenteredButton(SpriteBatch batch, Texture texture, Rectangle bounds, boolean hovered) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float scale = hovered ? GameConfig.UI_HOVER_SCALE : 1.0f;

        float drawWidth = bounds.width * scale;
        float drawHeight = bounds.height * scale;
        float drawX = centerX - drawWidth / 2f;
        float drawY = bounds.y - (drawHeight - bounds.height) / 2f;

        applyButtonTint(batch, hovered);
        batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
        batch.setColor(Color.WHITE);
    }

    private void applyButtonTint(SpriteBatch batch, boolean hovered) {
        if (hovered) {
            batch.setColor(Color.WHITE);
        } else {
            float tint = GameConfig.UI_INACTIVE_TINT;
            batch.setColor(tint, tint, tint, 1f);
        }
    }
}