package project.roguelike.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Scene {
    void create();

    void update(float delta);

    void render(SpriteBatch batch);

    void dispose();

    void resize(int width, int height);
}
