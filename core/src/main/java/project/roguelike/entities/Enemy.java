package project.roguelike.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

    private Texture texture;
    private Vector2 position;
    private float speed = 100f;
    private Rectangle bounds;
    private int health = 3;

    private float attackCooldown = 1.0f;
    private float timeSinceLastAttack = 0;
    private int damage = 1;

    public Enemy(float x, float y) {
        texture = new Texture("textures/orc.png");
        position = new Vector2(x, y);
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta, Vector2 playerPosition, Rectangle playerBounds, Player player) {
        timeSinceLastAttack += delta;

        Vector2 toPlayer = new Vector2(playerPosition).sub(position);
        float distance = toPlayer.len();

        Vector2 direction = toPlayer.nor();

        float stopDistance = texture.getWidth() * 0.75f;

        if (distance > stopDistance) {
            position.add(direction.scl(speed * delta));
        } else {
            float penetration = stopDistance - distance;
            position.sub(direction.scl(penetration));
        }
        position.x = Math.max(0, Math.min(Gdx.graphics.getWidth() - texture.getWidth(), position.x));
        position.y = Math.max(0, Math.min(Gdx.graphics.getHeight() - texture.getHeight(), position.y));
        bounds.setPosition(position);

        if (bounds.overlaps(playerBounds) && timeSinceLastAttack >= attackCooldown) {
            player.takeDamage(damage);
            timeSinceLastAttack = 0f;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void dispose() {
        texture.dispose();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void takeDamage(int amount) {
        health -= amount;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean collides(Rectangle other) {
        return bounds.overlaps(other);
    }
}
