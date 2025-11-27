package project.roguelike.items.passiveItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class MaxHpBoost extends PassiveItem {
    private static final int HP_BONUS = 2;
    private static final String ITEM_ID = "maxHpBoost";
    private static final String ITEM_NAME = "Max HP Boost";

    public MaxHpBoost() {
        super(
                ITEM_ID,
                ITEM_NAME,
                1.0f,
                HP_BONUS,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f);

        Texture spriteSheet = new Texture("textures/hp_boost.png");
        initializeAnimation(spriteSheet);
    }

    public MaxHpBoost(Vector2 position) {
        this();
        setPosition(position);
    }
}