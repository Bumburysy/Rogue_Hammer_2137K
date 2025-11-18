package project.roguelike.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.core.GameStatistics;
import project.roguelike.rooms.Room;

public class Bullet {
    private static final float DEFAULT_SPEED = GameConfig.BULLET_SPEED;
    private static final float DEFAULT_DAMAGE = 1f;
    private static final float HALF_SIZE = GameConfig.BULLET_SIZE / 2f;

    private final Texture texture;

    private final Vector2 position;
    private final Vector2 velocity;
    private final Rectangle bounds;

    private float speed = DEFAULT_SPEED;
    private float damage = DEFAULT_DAMAGE;
    private boolean active = true;

    private GameStatistics statistics;

    public Bullet(float x, float y, Vector2 direction) {
        this.texture = new Texture("textures/bullet.png");
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(direction).nor();
        this.bounds = new Rectangle(x - HALF_SIZE, y - HALF_SIZE, GameConfig.BULLET_SIZE, GameConfig.BULLET_SIZE);
    }

    public void setStatistics(GameStatistics statistics) {
        this.statistics = statistics;
    }

    public void update(float delta) {
        if (!active) {
            return;
        }

        position.add(velocity.cpy().scl(speed * delta));
        bounds.setPosition(position.x - HALF_SIZE, position.y - HALF_SIZE);
    }

    public void render(SpriteBatch batch) {
        if (!active) {
            return;
        }

        batch.draw(texture, position.x - HALF_SIZE, position.y - HALF_SIZE,
                GameConfig.BULLET_SIZE, GameConfig.BULLET_SIZE);
    }

    public void checkRoomBounds(Room room) {
        if (!isInBounds(room)) {
            active = false;
        }
    }

    public void hitEnemy(Enemy enemy) {
        if (!active || enemy == null) {
            return;
        }

        enemy.takeDamage(damage);

        if (statistics != null) {
            statistics.onBulletHit();
            statistics.onDamageDealt((int) damage);
        }

        active = false;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    private boolean isInBounds(Room room) {
        Vector2 roomPos = room.getPosition();
        return position.x >= roomPos.x &&
                position.x <= roomPos.x + GameConfig.ROOM_WIDTH &&
                position.y >= roomPos.y &&
                position.y <= roomPos.y + GameConfig.ROOM_HEIGHT;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    public float getDamage() {
        return damage;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }
}
