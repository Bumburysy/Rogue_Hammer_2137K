package project.roguelike.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import project.roguelike.core.InputAction;
import project.roguelike.core.InputManager;
import project.roguelike.core.SceneManager;
import project.roguelike.core.WorldManager;
import project.roguelike.levels.*;

public class GameScene implements Scene {
    private final SceneManager sceneManager;
    private WorldManager world;
    private InputManager inputManager;

    public GameScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void create() {
        world = new WorldManager(Layout2.layout, sceneManager);
        world.create();
        inputManager = sceneManager.getInputManager();
    }

    @Override
    public void update(float delta) {
        if (world != null) {
            world.update(delta);
        }

        if (inputManager.isActionJustPressed(InputAction.PAUSE)) {
            pauseGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (world != null) {
            world.resize(width, height);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (world == null) {
            return;
        }

        batch.begin();
        world.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        if (world != null) {
            world.dispose();
            world = null;
        }
    }

    private void pauseGame() {
        sceneManager.pushScene(new PauseMenuScene(sceneManager, this));
    }
}
