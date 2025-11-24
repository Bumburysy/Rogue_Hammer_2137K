package project.roguelike.items.currencyItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.entities.Player;
import project.roguelike.items.Item;

public abstract class CurrencyItem extends Item {
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 16;
    private static final int FRAME_COUNT = 4;
    private static final float FRAME_DURATION = 0.2f;

    protected Animation<TextureRegion> idleAnimation;
    protected float stateTime;
    protected final int value;

    protected CurrencyItem(String id, String name, int value) {
        super(id, name, ItemType.CURRENCY);
        this.value = value;
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

    public abstract void onPickup(Player player);

    public int getValue() {
        return value;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (texture != null) {
            texture.dispose();
        }
    }
}