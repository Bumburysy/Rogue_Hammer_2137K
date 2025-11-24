package project.roguelike.items.passiveItems;

import com.badlogic.gdx.graphics.Texture;

public class ReloadSpeedBoost extends PassiveItem {
    private static final float RELOAD_SPEED_MULTIPLIER = 0.8f;
    private static final String ITEM_ID = "reloadSpeedBoost";
    private static final String ITEM_NAME = "Reload Speed Boost";

    public ReloadSpeedBoost() {
        super(
                ITEM_ID,
                ITEM_NAME,
                1.0f,
                0,
                1.0f,
                RELOAD_SPEED_MULTIPLIER,
                1.0f,
                1.0f,
                1.0f);

        Texture spriteSheet = new Texture("textures/reload_boost.png");
        initializeAnimation(spriteSheet);
    }
}