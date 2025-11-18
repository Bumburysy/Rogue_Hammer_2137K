package project.roguelike.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import project.roguelike.scenes.MainMenuScene;

public class RogueHammerGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private SceneManager sceneManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();

        enableBlending();
        sceneManager.setScene(new MainMenuScene(sceneManager));
    }

    @Override
    public void render() {
        clearScreen();

        float delta = Gdx.graphics.getDeltaTime();
        sceneManager.update(delta);
        sceneManager.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        if (sceneManager != null) {
            sceneManager.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (sceneManager != null) {
            sceneManager.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }

    private void enableBlending() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
