package project.roguelike.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;

public class GoblinEnemy extends Enemy {
    private static final float VISUAL_SIZE_MULTIPLIER = 1.0f;
    private static final float COLLISION_WIDTH_MULTIPLIER = 0.8f;
    private static final float COLLISION_HEIGHT_MULTIPLIER = 0.8f;
    private static final int GOBLIN_MAX_HEALTH = 4;
    private static final float GOBLIN_SPEED = 220f;
    private static final int GOBLIN_DAMAGE = 1;
    private static final float ATTACK_COOLDOWN = 1.2f;
    private static final float ANIMATION_FRAME_DURATION = 0.2f;

    private Texture spriteSheet;
    private boolean facingRight = true;

    public GoblinEnemy(Vector2 spawnPos) {
        super(spawnPos);
        initializeGoblinStats();
        loadAnimations();
    }

    @Override
    protected void loadAnimations() {
        spriteSheet = new Texture(Gdx.files.internal("textures/orc.png"));
        TextureRegion region = new TextureRegion(spriteSheet);
        idleAnimation = new Animation<>(ANIMATION_FRAME_DURATION, region);
        attackAnimation = new Animation<>(ANIMATION_FRAME_DURATION, region);
    }

    @Override
    protected void performAttack(Player player) {
        if (player == null) {
            return;
        }

        attackTimer = 0f;
        currentState = State.ATTACK;

        if (position.dst(player.getPosition()) <= attackRange + getAttackRangeTolerance()) {
            player.takeDamage(damage);
        }
    }

    @Override
    protected void updateFacingDirection() {
        facingRight = velocity.x >= 0;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = getCurrentFrame();
        if (frame == null) {
            return;
        }

        Vector2 drawPos = getDrawPosition();

        if (dying || damageFlashTimer > 0) {
            batch.setColor(1f, 0f, 0f, 1f);
        }

        if (dying) {
            batch.draw(frame, drawPos.x, drawPos.y, width / 2f, height / 2f,
                    width, height, 1f, 1f, deathRotation);
        } else {
            float scaleX = facingRight ? 1f : -1f;
            batch.draw(frame, drawPos.x, drawPos.y, width / 2f, height / 2f,
                    width, height, scaleX, 1f, 0f);
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }

    private void initializeGoblinStats() {
        this.visualSizeMultiplier = VISUAL_SIZE_MULTIPLIER;
        this.collisionWidthMultiplier = COLLISION_WIDTH_MULTIPLIER;
        this.collisionHeightMultiplier = COLLISION_HEIGHT_MULTIPLIER;
        initializeSize();

        this.health = this.maxHealth = GOBLIN_MAX_HEALTH;
        this.speed = GOBLIN_SPEED;
        this.damage = GOBLIN_DAMAGE;
        this.agroRange = GameConfig.ROOM_WIDTH / 1.5f;
        this.attackRange = GameConfig.TILE_SIZE * 0.7f;
        this.attackCooldown = ATTACK_COOLDOWN;
        this.attackTimer = attackCooldown;
        this.type = Type.GOBLIN;
    }
}
