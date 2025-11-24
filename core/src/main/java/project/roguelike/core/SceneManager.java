package project.roguelike.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Stack;
import project.roguelike.scenes.Scene;
import project.roguelike.scenes.GameOverScene;

public class SceneManager {
    private final Stack<Scene> sceneStack = new Stack<>();
    private Scene backgroundScene = null;
    private final InputManager globalInputManager = new InputManager();

    public void setScene(Scene scene) {
        if (isGameOverTransition(scene)) {
            handleGameOverTransition();
        } else {
            handleNormalTransition();
        }

        sceneStack.push(scene);
        scene.create();
        scene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public Scene getCurrentScene() {
        return sceneStack.isEmpty() ? null : sceneStack.peek();
    }

    public void pushScene(Scene newScene) {
        sceneStack.push(newScene);
        newScene.create();
        newScene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void popScene() {
        if (sceneStack.isEmpty()) {
            return;
        }

        Scene popped = sceneStack.pop();
        cleanupPoppedScene(popped);

        Scene resumed = getCurrentScene();
        if (resumed != null) {
            resumed.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public void update(float delta) {
        Scene current = getCurrentScene();
        if (current != null) {
            current.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        if (shouldRenderBackground()) {
            backgroundScene.render(batch);
        }

        Scene current = getCurrentScene();
        if (current != null) {
            current.render(batch);
        }
    }

    public void resize(int width, int height) {
        Scene current = getCurrentScene();
        if (current != null) {
            current.resize(width, height);
        }
    }

    public void dispose() {
        disposeBackground();
        disposeSceneStack();
    }

    public InputManager getInputManager() {
        return globalInputManager;
    }

    private boolean isGameOverTransition(Scene scene) {
        return scene instanceof GameOverScene && !sceneStack.isEmpty();
    }

    private void handleGameOverTransition() {
        backgroundScene = sceneStack.peek();
    }

    private void handleNormalTransition() {
        disposeBackground();
        disposeSceneStack();
    }

    private void cleanupPoppedScene(Scene popped) {
        if (popped instanceof GameOverScene) {
            disposeBackground();
        }
        safeDispose(popped);
    }

    private boolean shouldRenderBackground() {
        return getCurrentScene() instanceof GameOverScene && backgroundScene != null;
    }

    private void disposeBackground() {
        if (backgroundScene != null) {
            safeDispose(backgroundScene);
            backgroundScene = null;
        }
    }

    private void disposeSceneStack() {
        while (!sceneStack.isEmpty()) {
            safeDispose(sceneStack.pop());
        }
    }

    private void safeDispose(Scene scene) {
        try {
            scene.dispose();
        } catch (Exception ignored) {
        }
    }
}
