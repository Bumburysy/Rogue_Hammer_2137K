package project.roguelike.items.passiveItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class MovementSpeedBoost extends PassiveItem {
    private static final float SPEED_MULTIPLIER = 1.2f;
    private static final String ITEM_ID = "movementSpeedBoost";
    private static final String ITEM_NAME = "Speed Boost";

    public MovementSpeedBoost() {
        super(
                ITEM_ID,
                ITEM_NAME,
                SPEED_MULTIPLIER,
                0,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f);

        Texture spriteSheet = new Texture("textures/speed_boost.png");
        initializeAnimation(spriteSheet);
    }

    public MovementSpeedBoost(Vector2 position) {
        this();
        setPosition(position);
    }
}