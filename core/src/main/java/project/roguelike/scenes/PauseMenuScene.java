package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import project.roguelike.core.SceneManager;

public class PauseMenuScene implements Scene {
    private final SceneManager sceneManager;
    private final GameScene gameScene;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private boolean resumeButtonPressed = false;
    private Texture overlayTexture;

    public PauseMenuScene(SceneManager sceneManager, GameScene gameScene) {
        this.sceneManager = sceneManager;
        this.gameScene = gameScene;
        font = new BitmapFont();
    }

    @Override
    public void create() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(0f, 0f, 0f, .7f);
        pm.fill();
        overlayTexture = new Texture(pm);
        pm.dispose();
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || resumeButtonPressed) {
            sceneManager.popScene();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1, 1, 1, 1);
        font.setColor(1, 1, 1, 1);
        String text = "GAME PAUSED";
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2f;
        font.draw(batch, text, x, y);
        String hint = "Press ESC to resume";
        layout.setText(font, hint);
        x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        y = y - layout.height * 2;
        font.draw(batch, hint, x, y);
    }

    @Override
    public void dispose() {
        font.dispose();
        if (overlayTexture != null) {
            overlayTexture.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (gameScene != null) {
            gameScene.resize(width, height);
        }
    }
}
