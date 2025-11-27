package project.roguelike.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.items.Item;
import project.roguelike.items.ItemFactory;
import java.util.Random;
import java.util.List;

public class Chest {
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 16;
    private static final float FRAME_DURATION = 0.15f;
    private static final float REMOVE_DELAY = 5f;
    private static final float FADE_DURATION = 2f;

    private final Vector2 position;
    private final Animation<TextureRegion> closedAnimation;
    private final Animation<TextureRegion> openAnimation;
    private float stateTime = 0f;
    private boolean opened = false;
    private boolean removed = false;
    private boolean dropsSpawned = false;
    private float openTime = 0f;
    private float fadeTime = 0f;
    private boolean fading = false;

    private List<Item> roomItemsRef = null;

    public Chest(Vector2 position) {
        this.position = new Vector2(position);
        Texture closedTexture = new Texture("textures/chest_closed.png");
        Texture openTexture = new Texture("textures/chest_open.png");
        TextureRegion[] closedFrames = TextureRegion.split(closedTexture, FRAME_WIDTH, FRAME_HEIGHT)[0];
        TextureRegion[] openFrames = TextureRegion.split(openTexture, FRAME_WIDTH, FRAME_HEIGHT)[0];
        closedAnimation = new Animation<>(FRAME_DURATION, closedFrames);
        openAnimation = new Animation<>(FRAME_DURATION, openFrames);
    }

    public void update(float delta) {
        stateTime += delta;
        if (opened) {
            openTime += delta;

            if (!dropsSpawned && stateTime >= openAnimation.getAnimationDuration()) {
                spawnDrops();
                dropsSpawned = true;
            }

            if (!fading && openTime >= REMOVE_DELAY) {
                fading = true;
                fadeTime = 0f;
            }

            if (fading) {
                fadeTime += delta;
                if (fadeTime >= FADE_DURATION) {
                    removed = true;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = opened
                ? openAnimation.getKeyFrame(stateTime, false)
                : closedAnimation.getKeyFrame(stateTime, true);

        float alpha = 1f;
        if (fading) {
            alpha = Math.max(0f, 1f - (fadeTime / FADE_DURATION));
        }

        float chestSize = GameConfig.TILE_SIZE;
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(frame,
                position.x - chestSize / 2f,
                position.y - chestSize / 2f,
                chestSize, chestSize);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isRemoved() {
        return removed;
    }

    public boolean tryOpen(Player player, List<Item> roomItems) {
        if (opened || removed)
            return false;
        if (player.getKeys() <= 0)
            return false;

        player.spendKeys(1);
        opened = true;
        stateTime = 0f;
        openTime = 0f;
        dropsSpawned = false;
        fading = false;
        fadeTime = 0f;
        this.roomItemsRef = roomItems;
        return true;
    }

    private void spawnDrops() {
        if (roomItemsRef == null)
            return;
        Random rand = new Random();
        int dropCount = 1 + rand.nextInt(2);

        float centerX = position.x;
        float centerY = position.y;
        Vector2 left = new Vector2(centerX - GameConfig.TILE_SIZE, centerY);
        Vector2 right = new Vector2(centerX + GameConfig.TILE_SIZE, centerY);

        if (dropCount == 1) {
            Item drop = ItemFactory.createRandomItem(left);
            if (drop != null) {
                roomItemsRef.add(drop);
            }
        } else if (dropCount == 2) {
            Item drop1 = ItemFactory.createRandomItem(left);
            Item drop2 = ItemFactory.createRandomItem(right);
            if (drop1 != null)
                roomItemsRef.add(drop1);
            if (drop2 != null)
                roomItemsRef.add(drop2);
        }
        roomItemsRef = null;
    }

    public com.badlogic.gdx.math.Rectangle getBounds() {
        return new com.badlogic.gdx.math.Rectangle(
                position.x,
                position.y,
                GameConfig.TILE_SIZE,
                GameConfig.TILE_SIZE);
    }
}