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
import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;

public class GameOverScene implements Scene {
    private static final float OVERLAY_ALPHA = 0.8f;
    private static final float FADE_SPEED = 1.5f;
    private static final float FADE_THRESHOLD = 0.5f;
    private static final float FONT_SCALE = 1.5f;
    private static final float STATS_START_Y = 550f;
    private static final float STATS_LINE_HEIGHT = 35f;
    private static final float STATS_HEADER_SPACING = 1.5f;

    private final SceneManager sceneManager;
    private GameStats stats;

    private Viewport viewport;
    private Texture overlayTexture;
    private Texture titleTexture;
    private Texture playAgainTexture;
    private Texture quitTexture;

    private Rectangle playAgainBounds;
    private Rectangle quitBounds;

    private boolean playAgainHovered = false;
    private boolean quitHovered = false;

    private BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private float fadeAlpha = 0f;
    private boolean fadeInDone = false;

    public static class GameStats {
        public final int enemiesKilled;
        public final int roomsCleared;
        public final int damageDealt;
        public final int damageTaken;
        public final float survivalTime;

        public GameStats(int enemiesKilled, int roomsCleared, int damageDealt, int damageTaken, float survivalTime) {
            this.enemiesKilled = enemiesKilled;
            this.roomsCleared = roomsCleared;
            this.damageDealt = damageDealt;
            this.damageTaken = damageTaken;
            this.survivalTime = survivalTime;
        }
    }

    public GameOverScene(SceneManager sceneManager, GameStats stats) {
        this.sceneManager = sceneManager;
        this.stats = stats != null ? stats : new GameStats(0, 0, 0, 0, 0f);
    }

    @Override
    public void create() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        viewport.apply(true);

        overlayTexture = createOverlayTexture();
        loadTextures();
        initializeFont();
        calculateButtonBounds();
    }

    @Override
    public void update(float delta) {
        updateFade(delta);
        updateHover();
        handleInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        renderOverlay(batch);

        if (fadeAlpha >= FADE_THRESHOLD) {
            ensureStatsValid();
            renderTitle(batch);
            renderStatistics(batch);
            renderButtons(batch);
        }

        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (overlayTexture != null)
            overlayTexture.dispose();
        if (titleTexture != null)
            titleTexture.dispose();
        if (playAgainTexture != null)
            playAgainTexture.dispose();
        if (quitTexture != null)
            quitTexture.dispose();
        if (font != null)
            font.dispose();
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
        titleTexture = new Texture("ui/game_over.png");
        playAgainTexture = new Texture("ui/play_again.png");
        quitTexture = new Texture("ui/quit.png");
    }

    private void initializeFont() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(FONT_SCALE);
    }

    private void calculateButtonBounds() {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float btnH = GameConfig.UI_BUTTON_HEIGHT;
        float startY = 250f;
        float spacing = GameConfig.UI_BUTTON_SPACING;

        float playAgainW = calculateButtonWidth(playAgainTexture, btnH);
        playAgainBounds = new Rectangle(centerX - playAgainW / 2f, startY, playAgainW, btnH);

        float quitW = calculateButtonWidth(quitTexture, btnH);
        quitBounds = new Rectangle(centerX - quitW / 2f, startY - spacing, quitW, btnH);
    }

    private float calculateButtonWidth(Texture texture, float height) {
        float aspect = (float) texture.getWidth() / texture.getHeight();
        return height * aspect;
    }

    private void updateFade(float delta) {
        if (!fadeInDone) {
            fadeAlpha += delta * FADE_SPEED;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                fadeInDone = true;
            }
        }
    }

    private void updateHover() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        playAgainHovered = playAgainBounds.contains(mouse.x, mouse.y);
        quitHovered = quitBounds.contains(mouse.x, mouse.y);
    }

    private void handleInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (playAgainHovered) {
                restartGame();
                return;
            }
            if (quitHovered) {
                returnToMenu();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            restartGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            returnToMenu();
        }
    }

    private void restartGame() {
        sceneManager.setScene(new LoadingScene(sceneManager, LoadingScene.SceneType.GAME, 0.8f));
    }

    private void returnToMenu() {
        sceneManager.setScene(new MainMenuScene(sceneManager));
    }

    private void renderOverlay(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, fadeAlpha);
        batch.draw(overlayTexture, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderTitle(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float titleHeight = GameConfig.UI_TITLE_HEIGHT;
        float titleWidth = calculateButtonWidth(titleTexture, titleHeight);
        float titleX = centerX - titleWidth / 2f;
        float titleY = GameConfig.WORLD_HEIGHT - titleHeight - GameConfig.UI_TITLE_TOP_MARGIN;

        batch.draw(titleTexture, titleX, titleY, titleWidth, titleHeight);
    }

    private void renderStatistics(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        float y = STATS_START_Y;

        font.setColor(Color.GOLD);
        drawCenteredText(batch, "=== GAME STATISTICS ===", centerX, y);

        font.setColor(Color.WHITE);
        y -= STATS_LINE_HEIGHT * STATS_HEADER_SPACING;

        drawCenteredText(batch, "Enemies Killed: " + stats.enemiesKilled, centerX, y);
        y -= STATS_LINE_HEIGHT;

        drawCenteredText(batch, "Rooms Cleared: " + stats.roomsCleared, centerX, y);
        y -= STATS_LINE_HEIGHT;

        drawCenteredText(batch, "Damage Dealt: " + stats.damageDealt, centerX, y);
        y -= STATS_LINE_HEIGHT;

        drawCenteredText(batch, "Damage Taken: " + stats.damageTaken, centerX, y);
        y -= STATS_LINE_HEIGHT;

        drawCenteredText(batch, formatSurvivalTime(), centerX, y);
    }

    private String formatSurvivalTime() {
        int minutes = (int) (stats.survivalTime / 60);
        int seconds = (int) (stats.survivalTime % 60);
        return String.format("Survival Time: %d:%02d", minutes, seconds);
    }

    private void renderButtons(SpriteBatch batch) {
        renderButton(batch, playAgainTexture, playAgainBounds, playAgainHovered);
        renderButton(batch, quitTexture, quitBounds, quitHovered);
    }

    private void renderButton(SpriteBatch batch, Texture texture, Rectangle bounds, boolean hovered) {
        float scale = hovered ? GameConfig.UI_HOVER_SCALE : 1f;
        float drawW = bounds.width * scale;
        float drawH = bounds.height * scale;
        float drawX = bounds.x - (drawW - bounds.width) / 2f;
        float drawY = bounds.y - (drawH - bounds.height) / 2f;

        float tint = hovered ? 1f : GameConfig.UI_BUTTON_INACTIVE_TINT;
        batch.setColor(tint, tint, tint, 1f);
        batch.draw(texture, drawX, drawY, drawW, drawH);
    }

    private void drawCenteredText(SpriteBatch batch, String text, float centerX, float y) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, centerX - glyphLayout.width / 2f, y);
    }

    private void ensureStatsValid() {
        if (stats == null) {
            stats = new GameStats(0, 0, 0, 0, 0f);
        }
    }
}
