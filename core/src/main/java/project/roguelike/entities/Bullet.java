package project.roguelike.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {

    private Texture textureUp;
    private Texture textureDown;
    private Texture textureLeft;
    private Texture textureRight;
    private Texture currentTexture;

    private Vector2 position;
    private Vector2 velocity;
    private float speed = 600f;
    private Rectangle bounds;

    public Bullet(float x, float y, Vector2 direction) {
        textureUp = new Texture("textures/bullet_up.png");
        textureDown = new Texture("textures/bullet_down.png");
        textureLeft = new Texture("textures/bullet_left.png");
        textureRight = new Texture("textures/bullet_right.png");

        if (direction.x > 0)
            currentTexture = textureRight;
        else if (direction.x < 0)
            currentTexture = textureLeft;
        else if (direction.y > 0)
            currentTexture = textureUp;
        else if (direction.y < 0)
            currentTexture = textureDown;

        position = new Vector2(x, y);
        velocity = direction.nor().scl(speed);
        bounds = new Rectangle(x, y, currentTexture.getWidth(), currentTexture.getHeight());
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(position);
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentTexture, position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isOffScreen() {
        return position.x < 0 || position.x > Gdx.graphics.getWidth()
                || position.y < 0 || position.y > Gdx.graphics.getHeight();
    }

    public void dispose() {
        textureUp.dispose();
        textureDown.dispose();
        textureLeft.dispose();
        textureRight.dispose();
    }
}
