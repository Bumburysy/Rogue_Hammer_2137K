package project.roguelike.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import project.roguelike.core.GameConfig;

public class Trap {
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 16;
    private static final float ANIMATION_SPEED = 0.15f;

    private static final Texture TRAP_TEXTURE = new Texture("textures/trap.png");
    private static final Animation<TextureRegion> ANIMATION;
    static {
        TextureRegion[][] tmp = TextureRegion.split(TRAP_TEXTURE, FRAME_WIDTH, FRAME_HEIGHT);
        ANIMATION = new Animation<>(ANIMATION_SPEED, tmp[0]);
    }

    private final Vector2 position;
    private final int damage;
    private float cooldown = 0f;
    private float animationTime = 0f;

    public Trap(Vector2 position, int damage) {
        this.position = position;
        this.damage = damage;
    }

    public void update(float delta, Player player) {
        animationTime += delta;
        if (cooldown > 0f) {
            cooldown -= delta;
            return;
        }
        float half = GameConfig.TILE_SIZE / 2f;
        Vector2 playerPos = player.getPosition();
        if (playerPos.x >= position.x - half && playerPos.x <= position.x + half &&
                playerPos.y >= position.y - half && playerPos.y <= position.y + half) {
            player.takeDamage(damage);
            cooldown = 1.0f;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = ANIMATION.getKeyFrame(animationTime, true);
        float size = GameConfig.TILE_SIZE;
        batch.draw(frame, position.x - size / 2f, position.y - size / 2f, size, size);
    }
}