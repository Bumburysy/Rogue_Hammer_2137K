package project.roguelike.items.currencyItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.entities.Player;

public class Key extends CurrencyItem {
    private static final String ITEM_ID = "key";
    private static final String ITEM_NAME = "Key";
    private static final int KEY_VALUE = 1;

    public Key() {
        super(ITEM_ID, ITEM_NAME, KEY_VALUE);
        Texture spriteSheet = new Texture("textures/key.png");
        initializeAnimation(spriteSheet);
    }

    public Key(Vector2 position, int value) {
        this();
        setPosition(position);
    }

    @Override
    public void onPickup(Player player) {
        player.addKeys(value);
    }
}