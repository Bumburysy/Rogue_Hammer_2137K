package project.roguelike.items.passiveItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.items.Item;

public abstract class PassiveItem extends Item {
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 16;
    private static final int FRAME_COUNT = 4;
    private static final float FRAME_DURATION = 0.2f;

    private final float speedMultiplier;
    private final int maxHpBonus;
    private final float damageMultiplier;
    private final float reloadSpeedMultiplier;
    private final float bulletSpeedMultiplier;
    private final float fireRateMultiplier;
    private final float magazineSizeMultiplier;

    protected Animation<TextureRegion> idleAnimation;
    protected float stateTime;

    protected PassiveItem(String id, String name,
            float speedMultiplier,
            int maxHpBonus,
            float damageMultiplier,
            float reloadSpeedMultiplier,
            float bulletSpeedMultiplier,
            float fireRateMultiplier,
            float magazineSizeMultiplier) {
        super(id, name, ItemType.PASSIVE);
        this.speedMultiplier = speedMultiplier;
        this.maxHpBonus = maxHpBonus;
        this.damageMultiplier = damageMultiplier;
        this.reloadSpeedMultiplier = reloadSpeedMultiplier;
        this.bulletSpeedMultiplier = bulletSpeedMultiplier;
        this.fireRateMultiplier = fireRateMultiplier;
        this.magazineSizeMultiplier = magazineSizeMultiplier;
        this.stateTime = 0f;
    }

    protected void initializeAnimation(Texture spriteSheet) {
        if (spriteSheet == null) {
            return;
        }

        this.texture = spriteSheet;

        TextureRegion[][] tmp = TextureRegion.split(
                spriteSheet,
                FRAME_WIDTH,
                FRAME_HEIGHT);

        TextureRegion[] frames = new TextureRegion[FRAME_COUNT];
        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = tmp[0][i];
        }

        idleAnimation = new Animation<>(FRAME_DURATION, frames);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        setTexture(frames[0].getTexture(), GameConfig.TILE_SIZE / 2f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateTime += delta;
    }

    @Override
    public void render(SpriteBatch batch, Vector2 position) {
        if (position == null || idleAnimation == null) {
            return;
        }

        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime);

        float size = GameConfig.TILE_SIZE;
        batch.draw(
                currentFrame,
                position.x - size / 2f,
                position.y - size / 2f,
                size,
                size);
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public int getMaxHpBonus() {
        return maxHpBonus;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public float getReloadSpeedMultiplier() {
        return reloadSpeedMultiplier;
    }

    public float getBulletSpeedMultiplier() {
        return bulletSpeedMultiplier;
    }

    public float getFireRateMultiplier() {
        return fireRateMultiplier;
    }

    public float getMagazineSizeMultiplier() {
        return magazineSizeMultiplier;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (texture != null) {
            texture.dispose();
        }
    }
}