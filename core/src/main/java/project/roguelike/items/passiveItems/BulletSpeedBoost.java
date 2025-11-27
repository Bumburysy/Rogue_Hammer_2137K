package project.roguelike.items.passiveItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class BulletSpeedBoost extends PassiveItem {
    private static final float BULLET_SPEED_MULTIPLIER = 1.2f;
    private static final String ITEM_ID = "bulletSpeedBoost";
    private static final String ITEM_NAME = "Bullet Speed Boost";

    public BulletSpeedBoost() {
        super(
                ITEM_ID,
                ITEM_NAME,
                1.0f,
                0,
                1.0f,
                1.0f,
                BULLET_SPEED_MULTIPLIER,
                1.0f,
                1.0f);

        Texture spriteSheet = new Texture("textures/bullet_speed_boost.png");
        initializeAnimation(spriteSheet);
    }

    public BulletSpeedBoost(Vector2 position) {
        this();
        setPosition(position);
    }
}