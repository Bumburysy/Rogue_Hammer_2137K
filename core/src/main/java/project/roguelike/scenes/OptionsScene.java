package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.UserSettings;
import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;

public class OptionsScene implements Scene {
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
    private Texture controlsLabelTexture;
    private Texture leftArrowTexture;
    private Texture rightArrowTexture;
    private BitmapFont font;

    private Texture sliderTrack;
    private Texture sliderHandle;

    private Rectangle backBounds;
    private Rectangle volumeLeft, volumeRight;
    private Rectangle resLeft, resRight;
    private Rectangle fullscreenToggle;
    private Rectangle controlsButton;

    private Rectangle masterSliderBounds;
    private Rectangle musicSliderBounds;
    private Rectangle uiSliderBounds;
    private Rectangle sfxSliderBounds;

    private float volumeLabelY;
    private float musicLabelY;
    private float uiLabelY;
    private float sfxLabelY;
    private float resolutionLabelY;
    private float fullscreenLabelY;
    private float controlsButtonY;

    private int currentResIndex;
    private boolean backHovered;
    private boolean volumeLeftHovered;
    private boolean volumeRightHovered;
    private boolean resLeftHovered;
    private boolean resRightHovered;
    private boolean fullscreenHovered;
    private boolean controlsHovered;

    private int draggingSlider = -1;

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
        calculateLayout();
        initializeBounds();
        findCurrentResolution();

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        sliderTrack = new Texture(pm);
        sliderHandle = new Texture(pm);
        pm.dispose();

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

        renderLabel(batch, volumeLabelTexture, GameConfig.WORLD_WIDTH / 2f, volumeLabelY);
        renderSlider(batch, masterSliderBounds, UserSettings.masterVolume);

        renderLabelText(batch, "Music Volume", GameConfig.WORLD_WIDTH / 2f, musicLabelY);
        renderSlider(batch, musicSliderBounds, UserSettings.musicVolume);

        renderLabelText(batch, "UI Volume", GameConfig.WORLD_WIDTH / 2f, uiLabelY);
        renderSlider(batch, uiSliderBounds, UserSettings.uiVolume);

        renderLabelText(batch, "SFX Volume", GameConfig.WORLD_WIDTH / 2f, sfxLabelY);
        renderSlider(batch, sfxSliderBounds, UserSettings.sfxVolume);

        renderResolutionSection(batch);
        renderFullscreenSection(batch);
        renderControlsButton(batch);

        renderBackButton(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        titleTexture.dispose();
        backTexture.dispose();
        volumeLabelTexture.dispose();
        resolutionLabelTexture.dispose();
        fullscreenLabelTexture.dispose();
        controlsLabelTexture.dispose();
        leftArrowTexture.dispose();
        rightArrowTexture.dispose();
        font.dispose();
        sliderTrack.dispose();
        sliderHandle.dispose();
    }

    private void loadTextures() {
        titleTexture = new Texture("ui/options.png");
        backTexture = new Texture("ui/back.png");
        volumeLabelTexture = new Texture("ui/volume.png");
        resolutionLabelTexture = new Texture("ui/resolution.png");
        fullscreenLabelTexture = new Texture("ui/fullscreen.png");
        controlsLabelTexture = new Texture("ui/controls.png");
        leftArrowTexture = new Texture("ui/left_arrow.png");
        rightArrowTexture = new Texture("ui/right_arrow.png");
    }

    private void initializeFont() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(GameConfig.UI_TEXT_SCALE);
    }

    private static class VerticalLayout {
        private float y;
        private final float spacing;

        private VerticalLayout(float startY, float spacing) {
            this.y = startY;
            this.spacing = spacing;
        }

        public static VerticalLayout fromTop(float spacing) {
            float startY = GameConfig.WORLD_HEIGHT - GameConfig.UI_TITLE_MARGIN_TOP - GameConfig.UI_TITLE_HEIGHT
                    - (GameConfig.UI_TITLE_MARGIN_BOTTOM / 2f);
            return new VerticalLayout(startY, spacing);
        }

        public float getCurrentY() {
            return y;
        }

        public float advance() {
            return advance(1);
        }

        public float advance(int steps) {
            y -= spacing * steps;
            return y;
        }
    }

    private void calculateLayout() {
        VerticalLayout layout = VerticalLayout.fromTop(GameConfig.UI_ELEMENT_SPACING / 2f);

        volumeLabelY = layout.getCurrentY();
        layout.advance();

        musicLabelY = layout.getCurrentY();
        layout.advance();

        uiLabelY = layout.getCurrentY();
        layout.advance();

        sfxLabelY = layout.getCurrentY();
        layout.advance();

        layout.advance();

        resolutionLabelY = layout.getCurrentY();
        layout.advance(2);

        fullscreenLabelY = layout.getCurrentY();
        layout.advance(2);

        controlsButtonY = layout.getCurrentY();
        layout.advance(2);
    }

    private void initializeBounds() {
        float centerX = GameConfig.WORLD_WIDTH / 2f;

        float trackWidth = 400f;
        float trackHeight = 8f;

        float masterSliderY = volumeLabelY - GameConfig.UI_VALUE_Y_OFFSET - 10f;
        float musicSliderY = musicLabelY - GameConfig.UI_VALUE_Y_OFFSET - 10f;
        float uiSliderY = uiLabelY - GameConfig.UI_VALUE_Y_OFFSET - 10f;
        float sfxSliderY = sfxLabelY - GameConfig.UI_VALUE_Y_OFFSET - 10f;

        masterSliderBounds = new Rectangle(centerX - trackWidth / 2f, masterSliderY, trackWidth, trackHeight);
        musicSliderBounds = new Rectangle(centerX - trackWidth / 2f, musicSliderY, trackWidth, trackHeight);
        uiSliderBounds = new Rectangle(centerX - trackWidth / 2f, uiSliderY, trackWidth, trackHeight);
        sfxSliderBounds = new Rectangle(centerX - trackWidth / 2f, sfxSliderY, trackWidth, trackHeight);

        float volumeValueY = volumeLabelY - GameConfig.UI_VALUE_Y_OFFSET;
        volumeLeft = createControlBounds(centerX - GameConfig.UI_MARGIN_SIDE, volumeValueY);
        volumeRight = createControlBounds(centerX + GameConfig.UI_MARGIN_SIDE, volumeValueY);

        float resolutionValueY = resolutionLabelY - GameConfig.UI_VALUE_Y_OFFSET;
        resLeft = createControlBounds(centerX - GameConfig.UI_MARGIN_SIDE, resolutionValueY);
        resRight = createControlBounds(centerX + GameConfig.UI_MARGIN_SIDE, resolutionValueY);

        float fullscreenValueY = fullscreenLabelY - GameConfig.UI_VALUE_Y_OFFSET;
        fullscreenToggle = new Rectangle(
                centerX - 50f,
                fullscreenValueY - 20f,
                100f,
                40f);

        controlsButton = createButtonBounds(controlsLabelTexture, centerX, controlsButtonY);
        backBounds = createButtonBounds(backTexture, centerX, GameConfig.UI_MARGIN_BOTTOM);
    }

    private Rectangle createButtonBounds(Texture texture, float centerX, float y) {
        float aspect = (float) texture.getWidth() / texture.getHeight();
        float width = GameConfig.UI_ELEMENT_HEIGHT * aspect;
        return new Rectangle(centerX - width / 2f, y, width, GameConfig.UI_ELEMENT_HEIGHT);
    }

    private Rectangle createControlBounds(float x, float y) {
        float size = GameConfig.UI_ELEMENT_HEIGHT * 0.5f;
        return new Rectangle(x - size / 2f, y - size / 2f, size, size);
    }

    private void findCurrentResolution() {
        String currentRes = UserSettings.getResolutionString();
        for (int i = 0; i < resolutions.length; i++) {
            if (resolutions[i].equals(currentRes)) {
                currentResIndex = i;
                return;
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
        controlsHovered = controlsButton.contains(mousePosition.x, mousePosition.y);
    }

    private void handleInput() {
        boolean justPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        boolean pressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        if (justPressed) {
            if (masterSliderBounds.contains(mousePosition.x, mousePosition.y)) {
                draggingSlider = 0;
            } else if (musicSliderBounds.contains(mousePosition.x, mousePosition.y)) {
                draggingSlider = 1;
            } else if (uiSliderBounds.contains(mousePosition.x, mousePosition.y)) {
                draggingSlider = 2;
            } else if (sfxSliderBounds.contains(mousePosition.x, mousePosition.y)) {
                draggingSlider = 3;
            }
        }

        if (draggingSlider != -1 && pressed) {
            Rectangle b = (draggingSlider == 0) ? masterSliderBounds
                    : (draggingSlider == 1) ? musicSliderBounds
                            : (draggingSlider == 2) ? uiSliderBounds
                                    : sfxSliderBounds;

            float t = (mousePosition.x - b.x) / b.width;
            t = Math.max(0f, Math.min(1f, t));
            switch (draggingSlider) {
                case 0:
                    UserSettings.setVolume(t);
                    project.roguelike.core.SoundManager.applyVolumes();
                    break;
                case 1:
                    UserSettings.setMusicVolume(t);
                    project.roguelike.core.SoundManager.applyVolumes();
                    break;
                case 2:
                    UserSettings.setUiVolume(t);
                    project.roguelike.core.SoundManager.applyVolumes();
                    break;
                case 3:
                    UserSettings.setSfxVolume(t);
                    break;
            }
            return;
        }

        if (!pressed) {
            draggingSlider = -1;
        }

        if (!justPressed || draggingSlider != -1) {
            return;
        }

        boolean clickedControl = backHovered
                || volumeLeftHovered
                || volumeRightHovered
                || resLeftHovered
                || resRightHovered
                || fullscreenHovered
                || controlsHovered;

        if (!clickedControl) {
            return;
        }

        project.roguelike.core.SoundManager.playButtonClick();

        if (backHovered) {
            handleBackButton();
        } else if (volumeLeftHovered) {
            UserSettings.setVolume(Math.max(0f, UserSettings.masterVolume - GameConfig.UI_VOLUME_STEP));
            project.roguelike.core.SoundManager.applyVolumes();
        } else if (volumeRightHovered) {
            UserSettings.setVolume(Math.min(1f, UserSettings.masterVolume + GameConfig.UI_VOLUME_STEP));
            project.roguelike.core.SoundManager.applyVolumes();
        } else if (resLeftHovered) {
            cycleResolution(-1);
        } else if (resRightHovered) {
            cycleResolution(1);
        } else if (fullscreenHovered) {
            UserSettings.toggleFullscreen();
        } else if (controlsHovered) {
            sceneManager.pushScene(new ControlsScene(sceneManager));
        }
    }

    private void handleBackButton() {
        if (usePop) {
            sceneManager.popScene();
        } else {
            sceneManager.setScene(new MainMenuScene(sceneManager));
        }
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

    private void renderLabelText(SpriteBatch batch, String text, float centerX, float y) {
        font.getData().setScale(GameConfig.UI_TEXT_SCALE);
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(batch, text, centerX - layout.width / 2f, y);
    }

    private void renderSlider(SpriteBatch batch, Rectangle bounds, float value) {
        batch.setColor(0.25f, 0.25f, 0.25f, 1f);
        batch.draw(sliderTrack, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(0.9f, 0.9f, 0.9f, 1f);
        batch.draw(sliderTrack, bounds.x, bounds.y, bounds.width * value, bounds.height);
        float handleW = 14f;
        float handleH = 18f;
        float handleX = bounds.x + bounds.width * value - handleW / 2f;
        float handleY = bounds.y + (bounds.height - handleH) / 2f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(sliderHandle, handleX, handleY, handleW, handleH);

        font.getData().setScale(GameConfig.UI_VALUE_TEXT_SCALE);
        String pct = Math.round(value * 100) + "%";
        font.draw(batch, pct, bounds.x + bounds.width + 8f, bounds.y + bounds.height);
        font.getData().setScale(GameConfig.UI_TEXT_SCALE);
    }

    private void renderTitle(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float aspect = (float) titleTexture.getWidth() / titleTexture.getHeight();
        float width = GameConfig.UI_TITLE_HEIGHT * aspect;
        float x = centerX - width / 2f;
        float y = GameConfig.WORLD_HEIGHT - GameConfig.UI_TITLE_HEIGHT - GameConfig.UI_TITLE_MARGIN_TOP;

        batch.draw(titleTexture, x, y, width, GameConfig.UI_TITLE_HEIGHT);
    }

    private void renderResolutionSection(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;

        renderLabel(batch, resolutionLabelTexture, centerX, resolutionLabelY);
        renderValueText(batch, resolutions[currentResIndex], centerX, resolutionLabelY);
        renderArrowControls(batch, resLeft, resRight, resLeftHovered, resRightHovered);
    }

    private void renderFullscreenSection(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;

        renderLabel(batch, fullscreenLabelTexture, centerX, fullscreenLabelY);

        String fsText = UserSettings.fullscreen ? "ON" : "OFF";
        float scale = fullscreenHovered ? GameConfig.UI_HOVER_SCALE : 1.0f;
        font.getData().setScale(GameConfig.UI_VALUE_TEXT_SCALE * scale);
        font.draw(batch, fsText, centerX - 20f, fullscreenLabelY - GameConfig.UI_VALUE_Y_OFFSET);
        font.getData().setScale(GameConfig.UI_TEXT_SCALE);
    }

    private void renderControlsButton(SpriteBatch batch) {
        renderButton(batch, controlsLabelTexture, controlsButton, controlsHovered);
    }

    private void renderBackButton(SpriteBatch batch) {
        renderButton(batch, backTexture, backBounds, backHovered);
    }

    private void renderLabel(SpriteBatch batch, Texture labelTexture, float centerX, float y) {
        float aspect = (float) labelTexture.getWidth() / labelTexture.getHeight();
        float width = GameConfig.UI_ELEMENT_HEIGHT * aspect;
        batch.draw(labelTexture, centerX - width / 2f, y, width, GameConfig.UI_ELEMENT_HEIGHT);
    }

    private void renderValueText(SpriteBatch batch, String text, float centerX, float y) {
        font.setColor(Color.WHITE);
        font.getData().setScale(GameConfig.UI_VALUE_TEXT_SCALE);
        float textX = text.length() > 5 ? centerX - 50f : centerX - 20f;
        font.draw(batch, text, textX, y - GameConfig.UI_VALUE_Y_OFFSET);
        font.getData().setScale(GameConfig.UI_TEXT_SCALE);
    }

    private void renderArrowControls(SpriteBatch batch, Rectangle leftBounds, Rectangle rightBounds,
            boolean leftHovered, boolean rightHovered) {
        renderArrow(batch, leftArrowTexture, leftBounds, leftHovered);
        renderArrow(batch, rightArrowTexture, rightBounds, rightHovered);
        batch.setColor(Color.WHITE);
    }

    private void renderArrow(SpriteBatch batch, Texture arrowTexture, Rectangle bounds, boolean hovered) {
        float scale = hovered ? GameConfig.UI_HOVER_SCALE : 1f;
        float drawWidth = bounds.width * scale;
        float drawHeight = bounds.height * scale;
        float drawX = bounds.x - (drawWidth - bounds.width) / 2f;
        float drawY = bounds.y - (drawHeight - bounds.height) / 2f;

        applyButtonTint(batch, hovered);
        batch.draw(arrowTexture, drawX, drawY, drawWidth, drawHeight);
    }

    private void renderButton(SpriteBatch batch, Texture texture, Rectangle bounds, boolean hovered) {
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
