package project.roguelike.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;

public class OrcEnemy extends Enemy {
    private static final float VISUAL_SIZE_MULTIPLIER = 1.5f;
    private static final float COLLISION_WIDTH_MULTIPLIER = 1.25f;
    private static final float COLLISION_HEIGHT_MULTIPLIER = 1.25f;
    private static final int ORC_MAX_HEALTH = 16;
    private static final float ORC_SPEED = 180f;
    private static final int ORC_DAMAGE = 2;
    private static final float ATTACK_COOLDOWN = 2f;
    private static final float ANIMATION_FRAME_DURATION = 0.2f;
    private static final int ANIMATION_FRAMES = 4;

    private Texture spriteSheet;
    private boolean facingRight = true;
    private static final float ATTACK_SWING_DURATION = 0.6f;

    public OrcEnemy(Vector2 spawnPos) {
        super(spawnPos);
        initializeOrcStats();
        loadAnimations();
    }

    @Override
    protected void loadAnimations() {
        spriteSheet = new Texture(Gdx.files.internal("textures/GoblinFighter.png"));
        int frameCount = ANIMATION_FRAMES;
        int frameW = spriteSheet.getWidth() / frameCount;
        int frameH = Math.max(1, spriteSheet.getHeight());
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameW, frameH);
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = tmp[0][i];
        }
        idleAnimation = new Animation<>(ANIMATION_FRAME_DURATION, frames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        attackAnimation = new Animation<>(ANIMATION_FRAME_DURATION, frames);
        attackAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        dieAnimation = new Animation<>(ANIMATION_FRAME_DURATION, new TextureRegion[] { frames[0] });
        dieAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    @Override
    protected void performAttack(Player player) {
        if (player == null) {
            return;
        }

        attackTimer = 0f;
        currentState = State.ATTACK;
        triggerAttackSwing(ATTACK_SWING_DURATION);
        if (position.dst(player.getPosition()) <= attackRange + getAttackRangeTolerance()) {
            player.takeDamage(damage);
        }
    }

    @Override
    protected void updateFacingDirection() {
        facingRight = velocity.x >= 0;
    }

    @Override
    public void update(float delta, Player player) {
        super.update(delta, player);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = (idleAnimation != null) ? idleAnimation.getKeyFrame(stateTime, true) : getCurrentFrame();
        if (frame == null) {
            return;
        }

        Vector2 drawPos = getDrawPosition();

        if (dying || damageFlashTimer > 0) {
            batch.setColor(1f, 0f, 0f, 1f);
        }

        float rotation = getAttackRotation(facingRight, 45f);

        if (dying) {
            batch.draw(frame, drawPos.x, drawPos.y, width / 2f, height / 2f,
                    width, height, 1f, 1f, deathRotation);
        } else {
            float scaleX = facingRight ? 1f : -1f;
            batch.draw(frame, drawPos.x, drawPos.y, width / 2f, height / 2f,
                    width, height, scaleX, 1f, rotation);
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }

    private void initializeOrcStats() {
        this.visualSizeMultiplier = VISUAL_SIZE_MULTIPLIER;
        this.collisionWidthMultiplier = COLLISION_WIDTH_MULTIPLIER;
        this.collisionHeightMultiplier = COLLISION_HEIGHT_MULTIPLIER;
        initializeSize();

        this.health = this.maxHealth = ORC_MAX_HEALTH;
        this.speed = ORC_SPEED;
        this.damage = ORC_DAMAGE;
        this.agroRange = GameConfig.ROOM_WIDTH / 1.5f;
        this.attackRange = GameConfig.TILE_SIZE * VISUAL_SIZE_MULTIPLIER * 0.7f;
        this.attackCooldown = ATTACK_COOLDOWN;
        this.attackTimer = attackCooldown;
        this.type = Type.ORC;
    }
}
