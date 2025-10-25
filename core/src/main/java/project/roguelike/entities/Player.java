package project.roguelike.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {

    private Texture textureUp;
    private Texture textureDown;
    private Texture textureLeft;
    private Texture textureRight;
    private Texture currentTexture;

    private Vector2 position;
    private float speed = 300f;
    private Rectangle bounds;

    private List<Bullet> bullets;
    private float shootCooldown = 0.2f;
    private float timeSinceLastShot = 0;
    private Vector2 lastShootDir = null;

    public Player(float x, float y) {
        textureUp = new Texture("textures/player_up.png");
        textureDown = new Texture("textures/player_down.png");
        textureLeft = new Texture("textures/player_left.png");
        textureRight = new Texture("textures/player_right.png");
        currentTexture = textureDown;

        position = new Vector2(x, y);
        bounds = new Rectangle(x, y, currentTexture.getWidth(), currentTexture.getHeight());

        bullets = new ArrayList<>();
    }

    private Vector2 getInputDirection(int up, int down, int left, int right) {
        Vector2 dir = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(up))
            dir.y += 1;
        if (Gdx.input.isKeyPressed(down))
            dir.y -= 1;
        if (Gdx.input.isKeyPressed(left))
            dir.x -= 1;
        if (Gdx.input.isKeyPressed(right))
            dir.x += 1;
        return dir.isZero() ? null : dir.nor();
    }

    public void update(float delta) {
        timeSinceLastShot += delta;

        Vector2 moveDir = getInputDirection(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);
        if (moveDir != null)
            position.add(moveDir.scl(speed * delta));

        position.x = Math.max(0, Math.min(Gdx.graphics.getWidth() - currentTexture.getWidth(), position.x));
        position.y = Math.max(0, Math.min(Gdx.graphics.getHeight() - currentTexture.getHeight(), position.y));
        bounds.setPosition(position);

        Vector2 shootDir = getInputDirection(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
        if (shootDir != null && timeSinceLastShot >= shootCooldown) {
            float bulletX = position.x;
            float bulletY = position.y;
            bullets.add(new Bullet(bulletX, bulletY, shootDir));
            lastShootDir = shootDir;
            timeSinceLastShot = 0;
        }

        if (lastShootDir != null && shootDir != null) {
            currentTexture = getTextureForDirection(lastShootDir);
        } else if (moveDir != null) {
            currentTexture = getTextureForDirection(moveDir);
        }

        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            b.update(delta);
            if (b.isOffScreen())
                iter.remove();
        }
    }

    private Texture getTextureForDirection(Vector2 dir) {
        if (dir.y > 0)
            return textureUp;
        if (dir.y < 0)
            return textureDown;
        if (dir.x < 0)
            return textureLeft;
        if (dir.x > 0)
            return textureRight;
        return currentTexture;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentTexture, position.x, position.y);
        for (Bullet b : bullets) {
            b.render(batch);
        }
    }

    public void dispose() {
        textureUp.dispose();
        textureDown.dispose();
        textureLeft.dispose();
        textureRight.dispose();
        for (Bullet b : bullets)
            b.dispose();
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}
