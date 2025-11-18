package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;

public class LoadingScene implements Scene {
    private static final float FADE_SPEED = 2.5f;
    private static final float PROGRESS_BAR_WIDTH = 400f;
    private static final float PROGRESS_BAR_HEIGHT = 30f;
    private static final float LOADING_SPEED = 1.5f;
    private static final float SCENE_CREATION_THRESHOLD = 0.7f;
    private static final float DEFAULT_MIN_DISPLAY_TIME = 0.5f;
    private static final float FONT_SCALE = 2f;
    private static final float LOGO_HEIGHT_MULTIPLIER = 0.8f;
    private static final float LOGO_Y_OFFSET = 100f;
    private static final float BAR_Y_OFFSET = -50f;
    private static final float TEXT_Y_OFFSET = -20f;

    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.3f, 1f);
    private static final Color BAR_BG_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private static final Color BAR_FILL_COLOR = new Color(0.2f, 0.7f, 0.2f, 1f);

    public enum SceneType {
        GAME, MAIN_MENU, OPTIONS
    }

    private final SceneManager sceneManager;
    private final SceneType targetSceneType;
    private final float minDisplayTime;

    private Scene targetScene;
    private Viewport viewport;
    private Texture backgroundTexture;
    private Texture logoTexture;
    private Texture progressBarBgTexture;
    private Texture progressBarFillTexture;
    private BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private float progress = 0f;
    private float displayTimer = 0f;
    private float fadeAlpha = 0f;
    private boolean fadeIn = true;
    private boolean fadeOut = false;
    private boolean targetSceneCreated = false;

    public LoadingScene(SceneManager sceneManager, SceneType targetSceneType, float minDisplayTime) {
        this.sceneManager = sceneManager;
        this.targetSceneType = targetSceneType;
        this.minDisplayTime = minDisplayTime;
    }

    public LoadingScene(SceneManager sceneManager, SceneType targetSceneType) {
        this(sceneManager, targetSceneType, DEFAULT_MIN_DISPLAY_TIME);
    }

    @Override
    public void create() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        viewport.apply(true);

        backgroundTexture = createSolidTexture(BACKGROUND_COLOR);
        logoTexture = loadLogoTexture();
        progressBarBgTexture = createSolidTexture(BAR_BG_COLOR);
        progressBarFillTexture = createSolidTexture(BAR_FILL_COLOR);

        initializeFont();
    }

    @Override
    public void update(float delta) {
        displayTimer += delta;

        updateFadeIn(delta);
        updateProgress(delta);
        updateFadeOut(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        renderBackground(batch);
        renderLogo(batch);
        renderProgressBar(batch);
        renderLoadingText(batch);
        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null)
            backgroundTexture.dispose();
        if (logoTexture != null)
            logoTexture.dispose();
        if (progressBarBgTexture != null)
            progressBarBgTexture.dispose();
        if (progressBarFillTexture != null)
            progressBarFillTexture.dispose();
        if (font != null)
            font.dispose();
    }

    private Texture createSolidTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture loadLogoTexture() {
        try {
            return new Texture("ui/title.png");
        } catch (Exception e) {
            return null;
        }
    }

    private void initializeFont() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(FONT_SCALE);
    }

    private void updateFadeIn(float delta) {
        if (fadeIn) {
            fadeAlpha += delta * FADE_SPEED;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                fadeIn = false;
            }
        }
    }

    private void updateProgress(float delta) {
        if (progress >= 1f) {
            return;
        }

        progress += delta * LOADING_SPEED;

        if (progress >= SCENE_CREATION_THRESHOLD && !targetSceneCreated) {
            createTargetScene();
            targetSceneCreated = true;
        }

        if (progress > 1f) {
            progress = 1f;
        }
    }

    private void updateFadeOut(float delta) {
        if (shouldStartFadeOut()) {
            fadeOut = true;
        }

        if (fadeOut) {
            fadeAlpha -= delta * FADE_SPEED;
            if (fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                transitionToTargetScene();
            }
        }
    }

    private boolean shouldStartFadeOut() {
        return progress >= 1f &&
                targetSceneCreated &&
                displayTimer >= minDisplayTime &&
                !fadeOut;
    }

    private void createTargetScene() {
        try {
            targetScene = createSceneByType(targetSceneType);
            initializeTargetScene(targetScene);
        } catch (Exception e) {
            targetScene = createFallbackScene();
            initializeTargetScene(targetScene);
        }
    }

    private Scene createSceneByType(SceneType type) {
        switch (type) {
            case GAME:
                return new GameScene(sceneManager);
            case MAIN_MENU:
                return new MainMenuScene(sceneManager);
            case OPTIONS:
                return new OptionsScene(sceneManager);
            default:
                throw new IllegalStateException("Unknown scene type: " + type);
        }
    }

    private Scene createFallbackScene() {
        return new MainMenuScene(sceneManager);
    }

    private void initializeTargetScene(Scene scene) {
        scene.create();
        scene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void transitionToTargetScene() {
        if (targetScene != null) {
            sceneManager.setScene(targetScene);
        } else {
            sceneManager.setScene(createFallbackScene());
        }
    }

    private void renderBackground(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, fadeAlpha);
        batch.draw(backgroundTexture, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderLogo(SpriteBatch batch) {
        if (logoTexture == null) {
            return;
        }

        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float centerY = GameConfig.WORLD_HEIGHT / 2f;

        float logoAspect = (float) logoTexture.getWidth() / logoTexture.getHeight();
        float logoHeight = GameConfig.UI_TITLE_HEIGHT * LOGO_HEIGHT_MULTIPLIER;
        float logoWidth = logoHeight * logoAspect;
        float logoX = centerX - logoWidth / 2f;
        float logoY = centerY + LOGO_Y_OFFSET;

        batch.setColor(1f, 1f, 1f, fadeAlpha);
        batch.draw(logoTexture, logoX, logoY, logoWidth, logoHeight);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderProgressBar(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float centerY = GameConfig.WORLD_HEIGHT / 2f;
        float barX = centerX - PROGRESS_BAR_WIDTH / 2f;
        float barY = centerY + BAR_Y_OFFSET;

        batch.setColor(1f, 1f, 1f, fadeAlpha);
        batch.draw(progressBarBgTexture, barX, barY, PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);

        float fillWidth = PROGRESS_BAR_WIDTH * progress;
        batch.draw(progressBarFillTexture, barX, barY, fillWidth, PROGRESS_BAR_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderLoadingText(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float centerY = GameConfig.WORLD_HEIGHT / 2f;
        float textY = centerY + BAR_Y_OFFSET + TEXT_Y_OFFSET;

        String loadingText = formatLoadingText();
        glyphLayout.setText(font, loadingText);

        font.setColor(1f, 1f, 1f, fadeAlpha);
        font.draw(batch, loadingText, centerX - glyphLayout.width / 2f, textY);
        font.setColor(Color.WHITE);
    }

    private String formatLoadingText() {
        return "LOADING... " + (int) (progress * 100) + "%";
    }
}
