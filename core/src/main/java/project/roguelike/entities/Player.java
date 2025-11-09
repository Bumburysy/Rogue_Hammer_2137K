package project.roguelike.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.rooms.Room;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private Texture spriteSheet;
    private TextureRegion[] frames;
    private TextureRegion[] framesFlipped;
    private final Vector2 tmpMouse = new Vector2();
    private float frameDuration = 0.08f;
    private float stateTime = 0f;
    private boolean facingLeft = false;
    private Vector2 position;
    private float speed = 400f;
    private int health = 20;
    private Rectangle bounds;
    private List<Bullet> bullets;
    private float shootCooldown = 0.2f;
    private float timeSinceLastShot = 0;

    public Player(float x, float y, float width, float height) {
        spriteSheet = new Texture("textures/player_spritesheet.png");
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 32, 32);
        int frameCount = tmp[0].length;
        frames = new TextureRegion[frameCount];
        framesFlipped = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = tmp[0][i];
            framesFlipped[i] = new TextureRegion(frames[i]);
            framesFlipped[i].flip(true, false);
        }
        position = new Vector2(x, y);
        bounds = new Rectangle(x - width / 2f, y - height / 2f, width, height);
        bullets = new ArrayList<>();
    }

    private Vector2 getInputDirection(int up, int down, int left, int right) {
        Vector2 dir = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(up)) {
            dir.y += 1;
        }
        if (Gdx.input.isKeyPressed(down)) {
            dir.y -= 1;
        }
        if (Gdx.input.isKeyPressed(left)) {
            dir.x -= 1;
        }
        if (Gdx.input.isKeyPressed(right)) {
            dir.x += 1;
        }
        return dir.isZero() ? null : dir.nor();
    }

    public void update(float delta, Room currentRoom, Viewport viewport) {
        timeSinceLastShot += delta;
        Vector2 moveDir = getInputDirection(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);

        if (moveDir != null) {
            position.add(moveDir.scl(speed * delta));
        }

        float minX = currentRoom.getPosition().x + bounds.height / 2;
        float maxX = currentRoom.getPosition().x + GameConfig.ROOM_WIDTH - bounds.height / 2;
        float minY = currentRoom.getPosition().y + bounds.height / 2;
        float maxY = currentRoom.getPosition().y + GameConfig.ROOM_HEIGHT - bounds.height / 2;
        position.x = Math.max(minX, Math.min(maxX, position.x));
        position.y = Math.max(minY, Math.min(maxY, position.y));
        bounds.setPosition(position.x - bounds.width / 2f, position.y - bounds.height / 2f);
        tmpMouse.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(tmpMouse);
        Vector2 shootDir = tmpMouse.cpy().sub(position).nor();

        if (tmpMouse.x < position.x) {
            facingLeft = true;
        } else if (tmpMouse.x > position.x) {
            facingLeft = false;
        }

        stateTime += delta;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && timeSinceLastShot >= shootCooldown) {
            float bulletSize = GameConfig.BULLET_SIZE;
            float bulletX = position.x - bulletSize / 2f;
            float bulletY = position.y - bulletSize / 2f;
            bullets.add(new Bullet(bulletX, bulletY, shootDir));
            timeSinceLastShot = 0;
        }

        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.update(delta);
            Vector2 bulletPos = bullet.getPosition();
            Vector2 roomPos = currentRoom.getPosition();

            if (bulletPos.x < roomPos.x ||
                    bulletPos.x > roomPos.x + GameConfig.ROOM_WIDTH ||
                    bulletPos.y < roomPos.y ||
                    bulletPos.y > roomPos.y + GameConfig.ROOM_HEIGHT) {
                bullet.dispose();
                iter.remove();
            }
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(SpriteBatch batch) {
        int idx = (int) (stateTime / frameDuration) % frames.length;
        TextureRegion region = facingLeft ? framesFlipped[idx] : frames[idx];
        batch.draw(region,
                position.x - bounds.width / 2f,
                position.y - bounds.height / 2f,
                bounds.width,
                bounds.height);
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
    }

    public void dispose() {
        if (spriteSheet != null)
            spriteSheet.dispose();
        for (Bullet b : bullets) {
            b.dispose();
        }
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void takeDamage(int amount) {
        health -= amount;
        System.out.println("Player HP: " + health);
    }

    public void heal(int amount) {
        health += amount;
    }

    public int getHealth() {
        return health;
    }

}