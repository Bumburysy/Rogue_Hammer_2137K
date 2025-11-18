package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;

public class PauseMenuScene implements Scene {
    private static final float OVERLAY_ALPHA = 0.7f;
    private static final float START_Y_OFFSET = 60f;

    private final SceneManager sceneManager;
    private final GameScene gameScene;
    private final Vector3 mousePosition = new Vector3();

    private Viewport viewport;
    private Texture overlayTexture;
    private Texture titleTexture;
    private Texture resumeTexture;
    private Texture optionsTexture;
    private Texture quitTexture;

    private Rectangle resumeBounds;
    private Rectangle optionsBounds;
    private Rectangle quitBounds;

    private boolean resumeHovered = false;
    private boolean optionsHovered = false;
    private boolean quitHovered = false;

    public PauseMenuScene(SceneManager sceneManager, GameScene gameScene) {
        this.sceneManager = sceneManager;
        this.gameScene = gameScene;
    }

    @Override
    public void create() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        viewport.apply(true);

        overlayTexture = createOverlayTexture();
        loadTextures();
        initializeBounds();
        showCursor();
    }

    @Override
    public void update(float delta) {
        updateMousePosition();
        updateHoveredStates();
        handleInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        // Renderuj grę w tle
        if (gameScene != null) {
            gameScene.render(batch);
        }

        // Nakładka pause menu
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        renderOverlay(batch);
        renderTitle(batch);
        renderButtons(batch);
        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (gameScene != null) {
            gameScene.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (overlayTexture != null)
            overlayTexture.dispose();
        if (titleTexture != null)
            titleTexture.dispose();
        if (resumeTexture != null)
            resumeTexture.dispose();
        if (optionsTexture != null)
            optionsTexture.dispose();
        if (quitTexture != null)
            quitTexture.dispose();
        hideCursor();
    }

    private Texture createOverlayTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, OVERLAY_ALPHA);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void loadTextures() {
        titleTexture = new Texture("ui/paused.png");
        resumeTexture = new Texture("ui/resume.png");
        optionsTexture = new Texture("ui/options.png");
        quitTexture = new Texture("ui/quit.png");
    }

    private void initializeBounds() {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float btnHeight = GameConfig.UI_BUTTON_HEIGHT;
        float startY = GameConfig.WORLD_HEIGHT / 2f + START_Y_OFFSET;
        float spacing = GameConfig.UI_BUTTON_SPACING;

        resumeBounds = createButtonBounds(resumeTexture, centerX, startY, btnHeight);
        optionsBounds = createButtonBounds(optionsTexture, centerX, startY - spacing, btnHeight);
        quitBounds = createButtonBounds(quitTexture, centerX, startY - spacing * 2, btnHeight);
    }

    private Rectangle createButtonBounds(Texture texture, float centerX, float y, float height) {
        float aspect = (float) texture.getWidth() / texture.getHeight();
        float width = height * aspect;
        return new Rectangle(centerX - width / 2f, y, width, height);
    }

    private void showCursor() {
        Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
    }

    private void hideCursor() {
        Gdx.graphics.setSystemCursor(SystemCursor.None);
    }

    private void updateMousePosition() {
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePosition);
    }

    private void updateHoveredStates() {
        resumeHovered = resumeBounds.contains(mousePosition.x, mousePosition.y);
        optionsHovered = optionsBounds.contains(mousePosition.x, mousePosition.y);
        quitHovered = quitBounds.contains(mousePosition.x, mousePosition.y);
    }

    private void handleInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            handleMouseClick();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
        }
    }

    private void handleMouseClick() {
        if (resumeHovered) {
            resumeGame();
        } else if (optionsHovered) {
            openOptions();
        } else if (quitHovered) {
            quitToMenu();
        }
    }

    private void resumeGame() {
        sceneManager.popScene();
    }

    private void openOptions() {
        sceneManager.pushScene(new OptionsScene(sceneManager, true));
    }

    private void quitToMenu() {
        sceneManager.setScene(new MainMenuScene(sceneManager));
    }

    private void renderOverlay(SpriteBatch batch) {
        batch.draw(overlayTexture, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
    }

    private void renderTitle(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float aspect = (float) titleTexture.getWidth() / titleTexture.getHeight();
        float height = GameConfig.UI_TITLE_HEIGHT;
        float width = height * aspect;
        float x = centerX - width / 2f;
        float y = GameConfig.WORLD_HEIGHT - height - GameConfig.UI_TITLE_TOP_MARGIN;

        batch.setColor(Color.WHITE);
        batch.draw(titleTexture, x, y, width, height);
    }

    private void renderButtons(SpriteBatch batch) {
        renderButton(batch, resumeTexture, resumeBounds, resumeHovered);
        renderButton(batch, optionsTexture, optionsBounds, optionsHovered);
        renderButton(batch, quitTexture, quitBounds, quitHovered);
    }

    private void renderButton(SpriteBatch batch, Texture texture, Rectangle bounds, boolean hovered) {
        float scale = hovered ? GameConfig.UI_HOVER_SCALE : 1f;
        float drawWidth = bounds.width * scale;
        float drawHeight = bounds.height * scale;
        float drawX = bounds.x - (drawWidth - bounds.width) / 2f;
        float drawY = bounds.y - (drawHeight - bounds.height) / 2f;

        applyButtonTint(batch, hovered);
        batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
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
