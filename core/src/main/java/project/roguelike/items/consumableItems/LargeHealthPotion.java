package project.roguelike.items.consumableItems;

import com.badlogic.gdx.graphics.Texture;
import project.roguelike.entities.Player;

public class LargeHealthPotion extends ConsumableItem {
    private static final int HEAL_AMOUNT = 10;

    public LargeHealthPotion() {
        super("largeHealthPotion", "Large Health Potion");

        Texture spriteSheet = new Texture("textures/potion_large.png");
        initializeAnimation(spriteSheet);
    }

    @Override
    public void onConsume(Player player) {
        int currentHealth = player.getHealth();
        int maxHealth = player.getMaxHealth();

        if (currentHealth < maxHealth) {
            player.heal(HEAL_AMOUNT);
            System.out.println("Consumed Large Health Potion! Healed " + HEAL_AMOUNT + " HP");
        } else {
            System.out.println("HP already full!");
        }
    }
}