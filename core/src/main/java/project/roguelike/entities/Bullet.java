package project.roguelike.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;

public class Bullet {
    private Texture texture;
    private Vector2 position;
    private Vector2 velocity;
    private float speed = project.roguelike.core.GameConfig.BULLET_SPEED;
    private Rectangle bounds;

    public Bullet(float x, float y, Vector2 direction) {
        texture = new Texture("textures/bullet.png");
        position = new Vector2(x, y);
        velocity = new Vector2(direction).nor();
        bounds = new Rectangle(
                x - GameConfig.BULLET_SIZE / 2f,
                y - GameConfig.BULLET_SIZE / 2f,
                GameConfig.BULLET_SIZE,
                GameConfig.BULLET_SIZE);
    }

    public void update(float delta) {
        position.add(velocity.x * speed * delta, velocity.y * speed * delta);
        bounds.setPosition(position);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - GameConfig.BULLET_SIZE / 2f,
                position.y - GameConfig.BULLET_SIZE / 2f,
                GameConfig.BULLET_SIZE, GameConfig.BULLET_SIZE);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        texture.dispose();
    }
}
