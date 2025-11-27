package project.roguelike.items.activeItems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.entities.Player;

public class MedKit extends ActiveItem {
    private static final int HEAL_AMOUNT = 5;
    private static final float COOLDOWN = 30f;

    public MedKit() {
        super("medkit", "Med Kit", COOLDOWN);
        this.texture = new Texture("textures/medkit.png");
        setTexture(texture, GameConfig.TILE_SIZE / 2f);
    }

    public MedKit(Vector2 position) {
        this();
        setPosition(position);
    }

    @Override
    protected void onUse(Player player) {
        int currentHealth = player.getHealth();
        int maxHealth = player.getMaxHealth();

        if (currentHealth < maxHealth) {
            player.heal(HEAL_AMOUNT);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (texture != null) {
            texture.dispose();
        }
    }
}