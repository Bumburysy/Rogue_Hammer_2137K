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
    private final SceneManager sceneManager;
    private GameStats stats;

    private Viewport viewport;
    private Texture overlayTexture;
    private Texture titleTexture;
    private Texture playAgainTexture;
    private Texture quitTexture;

    private Rectangle playAgainBounds;
    private Rectangle quitBounds;

    private boolean playAgainHovered;
    private boolean quitHovered;

    private BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private float fadeAlpha;
    private boolean fadeInDone;

    private static class VerticalLayout {
        private float y;
        private final float spacing;

        public VerticalLayout(float startY, float spacing) {
            this.y = startY;
            this.spacing = spacing;
        }

        public static VerticalLayout fromTop(float spacing) {
            float startY = GameConfig.WORLD_HEIGHT - GameConfig.UI_TITLE_MARGIN_TOP
                    - GameConfig.UI_TITLE_HEIGHT
                    - GameConfig.UI_TITLE_MARGIN_BOTTOM;
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

        if (fadeAlpha >= 0.5f) {
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
        overlayTexture.dispose();
        titleTexture.dispose();
        playAgainTexture.dispose();
        quitTexture.dispose();
        font.dispose();
    }

    private Texture createOverlayTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, GameConfig.UI_OVERLAY_ALPHA);
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
        font.getData().setScale(GameConfig.UI_TEXT_SCALE_SMALL);
    }

    private void calculateButtonBounds() {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        VerticalLayout layout = VerticalLayout.fromTop(GameConfig.UI_ELEMENT_SPACING_COMPACT);
        layout.advance(6);
        layout.advance();
        playAgainBounds = createCenteredButtonBounds(playAgainTexture, centerX, layout.getCurrentY());
        layout.advance();

        quitBounds = createCenteredButtonBounds(quitTexture, centerX, layout.getCurrentY());
    }

    private Rectangle createCenteredButtonBounds(Texture texture, float centerX, float y) {
        float aspect = (float) texture.getWidth() / texture.getHeight();
        float width = GameConfig.UI_ELEMENT_HEIGHT * aspect;
        return new Rectangle(centerX - width / 2f, y, width, GameConfig.UI_ELEMENT_HEIGHT);
    }

    private void updateFade(float delta) {
        if (!fadeInDone) {
            fadeAlpha += delta * GameConfig.UI_FADE_SPEED;
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
        sceneManager.setScene(
                new LoadingScene(sceneManager, LoadingScene.SceneType.GAME, GameConfig.UI_OVERLAY_ALPHA));
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
        float aspect = (float) titleTexture.getWidth() / titleTexture.getHeight();
        float width = GameConfig.UI_TITLE_HEIGHT * aspect;
        float x = centerX - width / 2f;
        float y = GameConfig.WORLD_HEIGHT - GameConfig.UI_TITLE_HEIGHT - GameConfig.UI_TITLE_MARGIN_TOP;

        batch.draw(titleTexture, x, y, width, GameConfig.UI_TITLE_HEIGHT);
    }

    private void renderStatistics(SpriteBatch batch) {
        float centerX = GameConfig.WORLD_WIDTH / 2f;
        VerticalLayout layout = VerticalLayout.fromTop(GameConfig.UI_ELEMENT_SPACING_COMPACT);
        font.getData().setScale(GameConfig.UI_VALUE_TEXT_SCALE);
        font.setColor(Color.GOLD);
        drawCenteredText(batch, "GAME STATISTICS", centerX, layout.getCurrentY());
        layout.advance();

        font.getData().setScale(GameConfig.UI_TEXT_SCALE_SMALL);
        font.setColor(Color.WHITE);

        drawCenteredText(batch, "Enemies Killed: " + stats.enemiesKilled, centerX, layout.getCurrentY());
        layout.advance();

        drawCenteredText(batch, "Rooms Cleared: " + stats.roomsCleared, centerX, layout.getCurrentY());
        layout.advance();

        drawCenteredText(batch, "Damage Dealt: " + stats.damageDealt, centerX, layout.getCurrentY());
        layout.advance();

        drawCenteredText(batch, "Damage Taken: " + stats.damageTaken, centerX, layout.getCurrentY());
        layout.advance();

        drawCenteredText(batch, formatSurvivalTime(), centerX, layout.getCurrentY());

        font.getData().setScale(GameConfig.UI_TEXT_SCALE_SMALL);
        font.setColor(Color.WHITE);
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

        float tint = hovered ? 1f : GameConfig.UI_INACTIVE_TINT;
        batch.setColor(tint, tint, tint, 1f);
        batch.draw(texture, drawX, drawY, drawW, drawH);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawCenteredText(SpriteBatch batch, String text, float centerX, float y) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, centerX - glyphLayout.width / 2f, y);
    }

    private String formatSurvivalTime() {
        int totalSeconds = (int) stats.survivalTime;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("Survival Time: %d:%02d", minutes, seconds);
    }

    private void ensureStatsValid() {
        if (stats == null) {
            stats = new GameStats(0, 0, 0, 0, 0f);
        }
    }
}
