package project.roguelike.items.currencyItems;

import com.badlogic.gdx.graphics.Texture;
import project.roguelike.entities.Player;

public class Coin extends CurrencyItem {
    private static final String ITEM_ID = "coin";
    private static final String ITEM_NAME = "Coin";
    private static final int COIN_VALUE = 1;

    public Coin() {
        super(ITEM_ID, ITEM_NAME, COIN_VALUE);
        Texture spriteSheet = new Texture("textures/coin.png");
        initializeAnimation(spriteSheet);
    }

    @Override
    public void onPickup(Player player) {
        player.addCoins(value);
    }
}