package project.roguelike.items.passiveItems;

import com.badlogic.gdx.graphics.Texture;

public class FireRateBoost extends PassiveItem {
    private static final float FIRE_RATE_MULTIPLIER = 0.8f;
    private static final String ITEM_ID = "fireRateBoost";
    private static final String ITEM_NAME = "Fire Rate Boost";

    public FireRateBoost() {
        super(
                ITEM_ID,
                ITEM_NAME,
                1.0f,
                0,
                1.0f,
                1.0f,
                1.0f,
                FIRE_RATE_MULTIPLIER,
                1.0f);

        Texture spriteSheet = new Texture("textures/fire_rate_boost.png");
        initializeAnimation(spriteSheet);
    }
}