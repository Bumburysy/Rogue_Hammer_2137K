package project.roguelike.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import project.roguelike.core.GameConfig;

public class Smg extends Weapon {
    public Smg() {
        super(
                "smg",
                "SMG",
                0.075f,
                500f,
                1f,
                30,
                1.4f,
                true);
        this.texture = new Texture("textures/smg.png");
        setTexture(texture, GameConfig.TILE_SIZE / 2F);
    }
}