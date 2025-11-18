package project.roguelike.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import project.roguelike.core.GameConfig;

public class Rifle extends Weapon {
    public Rifle() {
        super(
                "rifle",
                "Rifle",
                0.075f,
                600f,
                2f,
                30,
                1.8f,
                true);
        this.texture = new Texture("textures/rifle.png");
        setTexture(texture, GameConfig.TILE_SIZE / 2F);
    }
}
