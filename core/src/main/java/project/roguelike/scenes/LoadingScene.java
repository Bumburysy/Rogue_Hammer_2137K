package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;

public class LoadingScene implements Scene {
    private static final float SCENE_CREATION_THRESHOLD = 0.7f;
    private static final float DEFAULT_MIN_DISPLAY_TIME = 0.5f;

    private static final Color BACKGROUND_COLOR = new Color(0.25f, 0.25f, 0.25f, 1f);
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

    private float progress;
    private float displayTimer;
    private float fadeAlpha;
    private boolean fadeIn = true;
    private boolean fadeOut;
    private boolean targetSceneCreated;
    private ShapeRenderer shapeRenderer;

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

        shapeRenderer = new ShapeRenderer();

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
        backgroundTexture.dispose();
        if (logoTexture != null) {
            logoTexture.dispose();
        }
        progressBarBgTexture.dispose();
        progressBarFillTexture.dispose();
        font.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
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
        font.getData().setScale(GameConfig.UI_TEXT_SCALE);
    }

    private void updateFadeIn(float delta) {
        if (fadeIn) {
            fadeAlpha += delta * GameConfig.UI_FADE_SPEED;
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

        progress += delta * GameConfig.UI_LOADING_SPEED;

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
            fadeAlpha -= delta * GameConfig.UI_FADE_SPEED;
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
        float logoWidth = GameConfig.UI_TITLE_HEIGHT * logoAspect;
        float logoX = centerX - logoWidth / 2f;
        float logoY = centerY + GameConfig.UI_ELEMENT_SPACING;

        batch.setColor(1f, 1f, 1f, fadeAlpha);
        batch.draw(logoTexture, logoX, logoY, logoWidth, GameConfig.UI_TITLE_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderProgressBar(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float centerY = GameConfig.WORLD_HEIGHT / 2f;
        float barX = centerX - GameConfig.UI_PROGRESS_BAR_WIDTH / 2f;
        float barY = centerY - GameConfig.UI_ELEMENT_SPACING;

        batch.setColor(0.18f, 0.18f, 0.28f, fadeAlpha);
        batch.draw(progressBarBgTexture, barX, barY, GameConfig.UI_PROGRESS_BAR_WIDTH,
                GameConfig.UI_PROGRESS_BAR_HEIGHT);

        batch.setColor(0.3f, 0.85f, 0.4f, fadeAlpha);
        float fillWidth = GameConfig.UI_PROGRESS_BAR_WIDTH * progress;
        batch.draw(progressBarFillTexture, barX, barY, fillWidth, GameConfig.UI_PROGRESS_BAR_HEIGHT);

        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, fadeAlpha);
        shapeRenderer.rect(barX, barY, GameConfig.UI_PROGRESS_BAR_WIDTH, GameConfig.UI_PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();

        batch.begin();
    }

    private void renderLoadingText(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float centerY = GameConfig.WORLD_HEIGHT / 2f;
        float textY = centerY - GameConfig.UI_ELEMENT_SPACING - GameConfig.UI_PROGRESS_BAR_HEIGHT - 20f;

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
