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
import project.roguelike.core.UserSettings;
import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;

public class OptionsScene implements Scene {
    private static final float FONT_SCALE = 2f;
    private static final float CONTROL_HEIGHT = 32f;
    private static final float LABEL_HEIGHT = 48f;
    private static final int VOLUME_STEP = 10;

    private static final float VOLUME_Y = 550f;
    private static final float RESOLUTION_Y = 430f;
    private static final float FULLSCREEN_Y = 310f;
    private static final float BACK_Y = 100f;

    private static final float CONTROL_LEFT_OFFSET = 150f;
    private static final float CONTROL_RIGHT_OFFSET = 100f;
    private static final float TOGGLE_WIDTH = 300f;
    private static final float TOGGLE_HEIGHT = 64f;
    private static final float VALUE_TEXT_OFFSET_Y = 20f;

    private final SceneManager sceneManager;
    private final boolean usePop;
    private final String[] resolutions = { "1280x720", "1600x900", "1920x1080" };
    private final Vector3 mousePosition = new Vector3();

    private Viewport viewport;
    private Texture titleTexture;
    private Texture backTexture;
    private Texture volumeLabelTexture;
    private Texture resolutionLabelTexture;
    private Texture fullscreenLabelTexture;
    private Texture leftArrowTexture;
    private Texture rightArrowTexture;
    private BitmapFont font;

    private Rectangle backBounds;
    private Rectangle volumeLeft, volumeRight;
    private Rectangle resLeft, resRight;
    private Rectangle fullscreenToggle;

    private int currentResIndex = 0;
    private boolean backHovered = false;
    private boolean volumeLeftHovered = false;
    private boolean volumeRightHovered = false;
    private boolean resLeftHovered = false;
    private boolean resRightHovered = false;
    private boolean fullscreenHovered = false;

    public OptionsScene(SceneManager sceneManager) {
        this(sceneManager, false);
    }

    public OptionsScene(SceneManager sceneManager, boolean usePop) {
        this.sceneManager = sceneManager;
        this.usePop = usePop;
    }

    @Override
    public void create() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        viewport.apply(true);

        loadTextures();
        initializeFont();
        initializeBounds();
        findCurrentResolution();

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
        renderVolumeSection(batch);
        renderResolutionSection(batch);
        renderFullscreenSection(batch);
        renderBackButton(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (titleTexture != null)
            titleTexture.dispose();
        if (backTexture != null)
            backTexture.dispose();
        if (volumeLabelTexture != null)
            volumeLabelTexture.dispose();
        if (resolutionLabelTexture != null)
            resolutionLabelTexture.dispose();
        if (fullscreenLabelTexture != null)
            fullscreenLabelTexture.dispose();
        if (leftArrowTexture != null)
            leftArrowTexture.dispose();
        if (rightArrowTexture != null)
            rightArrowTexture.dispose();
        if (font != null)
            font.dispose();
    }

    private void loadTextures() {
        titleTexture = new Texture("ui/options.png");
        backTexture = new Texture("ui/back.png");
        volumeLabelTexture = new Texture("ui/volume.png");
        resolutionLabelTexture = new Texture("ui/resolution.png");
        fullscreenLabelTexture = new Texture("ui/fullscreen.png");
        leftArrowTexture = new Texture("ui/left_arrow.png");
        rightArrowTexture = new Texture("ui/right_arrow.png");
    }

    private void initializeFont() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(FONT_SCALE);
    }

    private void initializeBounds() {
        float centerX = GameConfig.WORLD_WIDTH / 2f;

        backBounds = createButtonBounds(backTexture, centerX, BACK_Y);
        volumeLeft = createControlBounds(centerX - CONTROL_LEFT_OFFSET, VOLUME_Y - 50f);
        volumeRight = createControlBounds(centerX + CONTROL_RIGHT_OFFSET, VOLUME_Y - 50f);
        resLeft = createControlBounds(centerX - CONTROL_LEFT_OFFSET, RESOLUTION_Y - 50f);
        resRight = createControlBounds(centerX + CONTROL_RIGHT_OFFSET, RESOLUTION_Y - 50f);
        fullscreenToggle = new Rectangle(centerX - TOGGLE_WIDTH / 2f, FULLSCREEN_Y - 50f, TOGGLE_WIDTH, TOGGLE_HEIGHT);
    }

    private Rectangle createButtonBounds(Texture texture, float centerX, float y) {
        float aspect = (float) texture.getWidth() / texture.getHeight();
        float height = GameConfig.UI_BUTTON_HEIGHT;
        float width = height * aspect;
        return new Rectangle(centerX - width / 2f, y, width, height);
    }

    private Rectangle createControlBounds(float x, float y) {
        return new Rectangle(x, y, CONTROL_HEIGHT, CONTROL_HEIGHT);
    }

    private void findCurrentResolution() {
        String currentRes = UserSettings.getResolutionString();
        for (int i = 0; i < resolutions.length; i++) {
            if (resolutions[i].equals(currentRes)) {
                currentResIndex = i;
                break;
            }
        }
    }

    private void updateMousePosition() {
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePosition);
    }

    private void updateHoveredStates() {
        backHovered = backBounds.contains(mousePosition.x, mousePosition.y);
        volumeLeftHovered = volumeLeft.contains(mousePosition.x, mousePosition.y);
        volumeRightHovered = volumeRight.contains(mousePosition.x, mousePosition.y);
        resLeftHovered = resLeft.contains(mousePosition.x, mousePosition.y);
        resRightHovered = resRight.contains(mousePosition.x, mousePosition.y);
        fullscreenHovered = fullscreenToggle.contains(mousePosition.x, mousePosition.y);
    }

    private void handleInput() {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            return;
        }

        if (backHovered) {
            handleBackButton();
        } else if (volumeLeftHovered) {
            adjustVolume(-VOLUME_STEP);
        } else if (volumeRightHovered) {
            adjustVolume(VOLUME_STEP);
        } else if (resLeftHovered) {
            cycleResolution(-1);
        } else if (resRightHovered) {
            cycleResolution(1);
        } else if (fullscreenHovered) {
            UserSettings.toggleFullscreen();
        }
    }

    private void handleBackButton() {
        if (usePop) {
            sceneManager.popScene();
        } else {
            sceneManager.setScene(new MainMenuScene(sceneManager));
        }
    }

    private void adjustVolume(int delta) {
        int currentVol = Math.round(UserSettings.masterVolume * 100);
        UserSettings.setVolume((currentVol + delta) / 100f);
    }

    private void cycleResolution(int direction) {
        currentResIndex = (currentResIndex + direction + resolutions.length) % resolutions.length;
        applyResolution();
    }

    private void applyResolution() {
        String[] parts = resolutions[currentResIndex].split("x");
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);
        UserSettings.setResolution(width, height);
    }

    private void renderTitle(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float aspect = (float) titleTexture.getWidth() / titleTexture.getHeight();
        float height = GameConfig.UI_TITLE_HEIGHT;
        float width = height * aspect;
        float x = centerX - width / 2f;
        float y = GameConfig.WORLD_HEIGHT - height - GameConfig.UI_TITLE_TOP_MARGIN;

        batch.draw(titleTexture, x, y, width, height);
    }

    private void renderVolumeSection(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;

        renderLabel(batch, volumeLabelTexture, centerX, VOLUME_Y);
        renderValueText(batch, (int) (UserSettings.masterVolume * 100) + "%", centerX, VOLUME_Y);
        renderArrowControls(batch, volumeLeft, volumeRight, volumeLeftHovered, volumeRightHovered);
    }

    private void renderResolutionSection(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;

        renderLabel(batch, resolutionLabelTexture, centerX, RESOLUTION_Y);
        renderValueText(batch, resolutions[currentResIndex], centerX, RESOLUTION_Y);
        renderArrowControls(batch, resLeft, resRight, resLeftHovered, resRightHovered);
    }

    private void renderFullscreenSection(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;

        renderLabel(batch, fullscreenLabelTexture, centerX, FULLSCREEN_Y);

        String fsText = UserSettings.fullscreen ? "ON" : "OFF";
        float scale = fullscreenHovered ? GameConfig.UI_HOVER_SCALE : 1.0f;
        font.getData().setScale(FONT_SCALE * scale);
        font.draw(batch, fsText, centerX - 20f, FULLSCREEN_Y - VALUE_TEXT_OFFSET_Y);
        font.getData().setScale(FONT_SCALE);
    }

    private void renderBackButton(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        renderButton(batch, backTexture, backBounds, centerX, backHovered);
    }

    private void renderLabel(SpriteBatch batch, Texture labelTexture, float centerX, float y) {
        float aspect = (float) labelTexture.getWidth() / labelTexture.getHeight();
        float width = LABEL_HEIGHT * aspect;
        batch.draw(labelTexture, centerX - width / 2f, y, width, LABEL_HEIGHT);
    }

    private void renderValueText(SpriteBatch batch, String text, float centerX, float y) {
        font.setColor(Color.WHITE);
        float textX = text.length() > 5 ? centerX - 50f : centerX - 20f;
        font.draw(batch, text, textX, y - VALUE_TEXT_OFFSET_Y);
    }

    private void renderArrowControls(SpriteBatch batch, Rectangle leftBounds, Rectangle rightBounds,
            boolean leftHovered, boolean rightHovered) {
        renderArrow(batch, leftArrowTexture, leftBounds, leftHovered);
        renderArrow(batch, rightArrowTexture, rightBounds, rightHovered);
        batch.setColor(Color.WHITE);
    }

    private void renderArrow(SpriteBatch batch, Texture arrowTexture, Rectangle bounds, boolean hovered) {
        float aspect = (float) arrowTexture.getWidth() / arrowTexture.getHeight();
        float height = bounds.height;
        float width = height * aspect;
        float scale = hovered ? GameConfig.UI_HOVER_SCALE : 1f;

        float drawWidth = width * scale;
        float drawHeight = height * scale;
        float drawX = bounds.x - (drawWidth - width) / 2f;
        float drawY = bounds.y - (drawHeight - height) / 2f;

        applyButtonTint(batch, hovered);
        batch.draw(arrowTexture, drawX, drawY, drawWidth, drawHeight);
    }

    private void renderButton(SpriteBatch batch, Texture texture, Rectangle bounds, float centerX, boolean hovered) {
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
            float tint = GameConfig.UI_BUTTON_INACTIVE_TINT;
            batch.setColor(tint, tint, tint, 1f);
        }
    }
}
