package project.roguelike.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Stack;
import project.roguelike.scenes.Scene;

public class SceneManager {
    private final Stack<Scene> scenes = new Stack<>();

    public void setScene(Scene newScene) {
        while (!scenes.isEmpty()) {
            scenes.pop().dispose();
        }
        scenes.push(newScene);
        newScene.create();
    }

    public void pushScene(Scene newScene) {
        scenes.push(newScene);
        newScene.create();
    }

    public void popScene() {
        if (!scenes.isEmpty()) {
            scenes.pop().dispose();
        }
    }

    public void update(float delta) {
        if (!scenes.isEmpty()) {
            scenes.peek().update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < scenes.size(); i++) {
            batch.end();
            batch.begin();
            scenes.get(i).render(batch);
        }
    }

    public void dispose() {
        while (!scenes.isEmpty()) {
            scenes.pop().dispose();
        }
    }

    public void resize(int width, int height) {
        if (!scenes.isEmpty()) {
            scenes.peek().resize(width, height);
        }
    }
}
