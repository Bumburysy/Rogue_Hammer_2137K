package project.roguelike.items.consumableItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.entities.Player;

public class LargeHealthPotion extends ConsumableItem {
    private static final int HEAL_AMOUNT = 10;

    public LargeHealthPotion() {
        super("largeHealthPotion", "Large Health Potion");

        Texture spriteSheet = new Texture("textures/potion_large.png");
        initializeAnimation(spriteSheet);
    }

    public LargeHealthPotion(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    public void onConsume(Player player) {
        int currentHealth = player.getHealth();
        int maxHealth = player.getMaxHealth();

        if (currentHealth < maxHealth) {
            player.heal(HEAL_AMOUNT);
        }
    }

    
}