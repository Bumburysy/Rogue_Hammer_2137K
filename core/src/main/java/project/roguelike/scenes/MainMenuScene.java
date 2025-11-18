package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;

public class MainMenuScene implements Scene {
    private static final int OPTION_COUNT = 3;
    private static final int PLAY_INDEX = 0;
    private static final int OPTIONS_INDEX = 1;
    private static final int EXIT_INDEX = 2;

    private final SceneManager sceneManager;
    private final Rectangle[] optionBounds = new Rectangle[OPTION_COUNT];
    private final Vector3 mousePosition = new Vector3();

    private Viewport viewport;
    private Texture titleTexture;
    private Texture playTexture;
    private Texture optionsTexture;
    private Texture quitTexture;

    private int hoveredIndex = -1;

    public MainMenuScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void create() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        viewport.apply(true);

        loadTextures();
        initializeBounds();
    }

    @Override
    public void update(float delta) {
        updateMousePosition();
        updateHoveredOption();
        handleInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        renderTitle(batch);
        renderOptions(batch);
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
        if (playTexture != null)
            playTexture.dispose();
        if (optionsTexture != null)
            optionsTexture.dispose();
        if (quitTexture != null)
            quitTexture.dispose();
    }

    private void loadTextures() {
        titleTexture = new Texture("ui/title.png");
        playTexture = new Texture("ui/play.png");
        optionsTexture = new Texture("ui/options.png");
        quitTexture = new Texture("ui/quit.png");
    }

    private void initializeBounds() {
        for (int i = 0; i < optionBounds.length; i++) {
            optionBounds[i] = new Rectangle();
        }
    }

    private void updateMousePosition() {
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePosition);
    }

    private void updateHoveredOption() {
        hoveredIndex = -1;
        for (int i = 0; i < OPTION_COUNT; i++) {
            if (optionBounds[i].contains(mousePosition.x, mousePosition.y)) {
                hoveredIndex = i;
                break;
            }
        }
    }

    private void handleInput() {
        if (hoveredIndex >= 0 && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            handleOptionSelection(hoveredIndex);
        }
    }

    private void handleOptionSelection(int index) {
        switch (index) {
            case PLAY_INDEX:
                startGame();
                break;
            case OPTIONS_INDEX:
                openOptions();
                break;
            case EXIT_INDEX:
                exitGame();
                break;
        }
    }

    private void startGame() {
        sceneManager.setScene(new LoadingScene(sceneManager, LoadingScene.SceneType.GAME, 1.0f));
    }

    private void openOptions() {
        sceneManager.setScene(new OptionsScene(sceneManager));
    }

    private void exitGame() {
        Gdx.app.exit();
    }

    private void renderTitle(SpriteBatch batch) {
        float aspect = (float) titleTexture.getWidth() / titleTexture.getHeight();
        float height = GameConfig.UI_TITLE_HEIGHT;
        float width = height * aspect;
        float x = (GameConfig.WORLD_WIDTH - width) / 2f;
        float y = GameConfig.WORLD_HEIGHT - height - GameConfig.UI_TITLE_TOP_MARGIN;

        batch.draw(titleTexture, x, y, width, height);
    }

    private void renderOptions(SpriteBatch batch) {
        Texture[] textures = { playTexture, optionsTexture, quitTexture };
        float startY = GameConfig.WORLD_HEIGHT / 2f;
        float spacing = GameConfig.UI_BUTTON_SPACING;

        for (int i = 0; i < textures.length; i++) {
            renderOption(batch, textures[i], i, startY - i * spacing);
        }
    }

    private void renderOption(SpriteBatch batch, Texture texture, int index, float y) {
        float aspect = (float) texture.getWidth() / texture.getHeight();
        float height = GameConfig.UI_BUTTON_HEIGHT;
        float width = height * aspect;

        boolean isHovered = (index == hoveredIndex);
        float scale = isHovered ? GameConfig.UI_HOVER_SCALE : 1.0f;
        float drawWidth = width * scale;
        float drawHeight = height * scale;
        float x = (GameConfig.WORLD_WIDTH - drawWidth) / 2f;

        applyButtonTint(batch, isHovered);
        batch.draw(texture, x, y, drawWidth, drawHeight);
        batch.setColor(Color.WHITE);

        optionBounds[index].set(x, y, drawWidth, drawHeight);
    }

    private void applyButtonTint(SpriteBatch batch, boolean isHovered) {
        if (isHovered) {
            batch.setColor(Color.WHITE);
        } else {
            float tint = GameConfig.UI_BUTTON_INACTIVE_TINT;
            batch.setColor(tint, tint, tint, 1f);
        }
    }
}