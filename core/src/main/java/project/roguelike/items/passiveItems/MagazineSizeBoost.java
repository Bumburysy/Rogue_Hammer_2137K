package project.roguelike.items.passiveItems;

import com.badlogic.gdx.graphics.Texture;

public class MagazineSizeBoost extends PassiveItem {
    private static final float MAGAZINE_MULTIPLIER = 1.2f;
    private static final String ITEM_ID = "magazineSizeBoost";
    private static final String ITEM_NAME = "Magazine Size Boost";

    public MagazineSizeBoost() {
        super(
                ITEM_ID,
                ITEM_NAME,
                1.0f,
                0,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                MAGAZINE_MULTIPLIER);

        Texture spriteSheet = new Texture("textures/magazine_boost.png");
        initializeAnimation(spriteSheet);
    }
}