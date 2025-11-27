package project.roguelike.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import project.roguelike.core.GameStatistics;
import project.roguelike.core.InputAction;
import project.roguelike.core.InputManager;
import project.roguelike.core.SceneManager;
import project.roguelike.core.WorldManager;
import project.roguelike.entities.Player;
import project.roguelike.levels.Layout1;
import project.roguelike.levels.Layout2;
import project.roguelike.levels.Layout3;
import project.roguelike.levels.Layout4;
import project.roguelike.levels.Layout5;
import project.roguelike.levels.Layout6;
import project.roguelike.levels.RoomData;

public class GameScene implements Scene {
    private final SceneManager sceneManager;
    private WorldManager world;
    private InputManager inputManager;
    private RoomData[][][] layouts;
    private int currentLayoutIdx = -1;

    public GameScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void create() {
        layouts = new RoomData[][][] {
                Layout1.layout,
                Layout2.layout,
                Layout3.layout,
                Layout4.layout,
                Layout5.layout,
                Layout6.layout
        };
        startNewLevel(null);
        inputManager = sceneManager.getInputManager();
    }

    public void startNewLevel(Player existingPlayer) {
        int nextIdx;
        if (layouts.length == 1) {
            nextIdx = 0;
        } else {
            do {
                nextIdx = (int) (Math.random() * layouts.length);
            } while (nextIdx == currentLayoutIdx);
        }
        currentLayoutIdx = nextIdx;

        if (existingPlayer == null) {
            world = new WorldManager(layouts[currentLayoutIdx], sceneManager);
            world.create();
            GameStatistics stats = world.getStatistics();
            if (stats != null) {
                stats.setCurrentLevel(1);
            }
        } else {
            world = new WorldManager(layouts[currentLayoutIdx], sceneManager, existingPlayer);
            world.create();
            GameStatistics stats = world.getStatistics();
            if (stats != null) {
                stats.incrementCurrentLevel();
            }
        }
    }

    @Override
    public void update(float delta) {
        if (world != null) {
            world.update(delta);

            Player player = world.getPlayer();
            if (player != null && player.isLevelTransitionRequested()) {
                sceneManager.pushScene(new LevelCompleteScene(sceneManager, this, player, world.getStatistics()));
                return;
            }
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
