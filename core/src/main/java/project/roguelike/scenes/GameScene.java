package project.roguelike.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import project.roguelike.core.SceneManager;
import project.roguelike.core.WorldManager;
import project.roguelike.levels.*;

public class GameScene implements Scene {
    private final SceneManager sceneManager;
    private WorldManager world;

    public GameScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void create() {
        world = new WorldManager(Layout1.layout);
        world.create();
        world.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void update(float delta) {
        world.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.pushScene(new PauseMenuScene(sceneManager, this));
        }
    }

    public void resize(int width, int height) {
        if (world != null) {
            world.resize(width, height);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        world.render(batch);
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}
