package project.roguelike;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Scene {
    void create();

    void render(SpriteBatch batch);

    void dispose();
}
