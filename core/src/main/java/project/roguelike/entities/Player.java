package project.roguelike.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    
    private Texture texture;
    private float x, y;
    private float speed = 200f;

    public Player() {
        texture = new Texture("textures/player.png");
        x = 400;
        y = 300;
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) y += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) x += speed * delta;

        x = Math.max(0, Math.min(Gdx.graphics.getWidth() - texture.getWidth(), x));
        y = Math.max(0, Math.min(Gdx.graphics.getHeight() - texture.getHeight(), y));
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
