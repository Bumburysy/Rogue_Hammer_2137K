package project.roguelike;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;

public class RogueHammerGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private Scene currentScene;
    private Scene roomTest;
    private Scene playerEnemyTest;

    @Override
    public void create() {
        batch = new SpriteBatch();

        roomTest = new RoomTest();
        playerEnemyTest = new PlayerEnemyTest();

        currentScene = roomTest;
        currentScene.create();
    }

    @Override
    public void render() {
        handleInput();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        currentScene.render(batch);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            switchScene(roomTest);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            switchScene(playerEnemyTest);
        }
    }

    private void switchScene(Scene newScene) {
        if (currentScene == newScene)
            return;
        if (currentScene != null)
            currentScene.dispose();
        currentScene = newScene;
        currentScene.create();
    }

    @Override
    public void dispose() {
        if (currentScene != null)
            currentScene.dispose();
        batch.dispose();
    }
}