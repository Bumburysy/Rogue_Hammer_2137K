package project.roguelike.items.passiveItems;

import com.badlogic.gdx.graphics.Texture;

public class DamageBoost extends PassiveItem {
    private static final float DAMAGE_MULTIPLIER = 1.2f;
    private static final String ITEM_ID = "damageBoost";
    private static final String ITEM_NAME = "Damage Boost";

    public DamageBoost() {
        super(
                ITEM_ID,
                ITEM_NAME,
                1.0f,
                0,
                DAMAGE_MULTIPLIER,
                1.0f,
                1.0f,
                1.0f,
                1.0f);

        Texture spriteSheet = new Texture("textures/damage_boost.png");
        initializeAnimation(spriteSheet);
    }
}