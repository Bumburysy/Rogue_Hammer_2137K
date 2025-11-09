package project.roguelike.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import project.roguelike.scenes.MainMenuScene;

public class RogueHammerGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private SceneManager sceneManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sceneManager = new SceneManager();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sceneManager.setScene(new MainMenuScene(sceneManager));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();
        sceneManager.update(delta);
        batch.begin();
        sceneManager.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (sceneManager != null) {
            sceneManager.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        batch.dispose();
    }
}
