package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import project.roguelike.core.GameConfig;
import project.roguelike.core.SceneManager;

public class MainMenuScene implements Scene {
    private final SceneManager sceneManager;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final String[] options = { "Play", "Options", "Exit" };
    private final Rectangle[] optionBounds = new Rectangle[options.length];
    private int hoveredIndex = -1;
    private OrthographicCamera camera;
    private Viewport viewport;

    public MainMenuScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f, 0);
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.4f);

        for (int i = 0; i < optionBounds.length; i++) {
            optionBounds[i] = new Rectangle();
        }
        System.out.println("Main Menu loaded!");
    }

    @Override
    public void update(float delta) {
        camera.update();
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);
        hoveredIndex = -1;

        for (int i = 0; i < options.length; i++) {
            if (optionBounds[i].contains(mousePos.x, mousePos.y)) {
                hoveredIndex = i;
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    handleOptionSelection(i);
                }
            }
        }
    }

    private void handleOptionSelection(int index) {
        switch (index) {
            case 0: {
                sceneManager.setScene(new GameScene(sceneManager));
                break;
            }
            case 1: {
                System.out.println("Options clicked");
                break;
            }
            case 2: {
                Gdx.app.exit();
                break;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        drawTitle(batch);
        drawOptions(batch);
    }

    private void drawTitle(SpriteBatch batch) {
        String title = "Rogue Hammer 2137k";
        layout.setText(font, title);
        float titleX = (GameConfig.WORLD_WIDTH - layout.width) / 2f;
        float titleY = GameConfig.WORLD_HEIGHT - layout.height * 3;
        font.draw(batch, title, titleX, titleY);
    }

    private void drawOptions(SpriteBatch batch) {
        float startY = (GameConfig.WORLD_HEIGHT - layout.height * 3) - 150;
        for (int i = 0; i < options.length; i++) {
            String text = options[i];
            layout.setText(font, text);
            float x = (GameConfig.WORLD_WIDTH - layout.width) / 2f;
            float y = startY - i * 80;
            font.setColor(i == hoveredIndex ? Color.SKY : Color.WHITE);
            font.draw(batch, text, x, y);
            optionBounds[i].set(x, y - layout.height, layout.width, layout.height);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}