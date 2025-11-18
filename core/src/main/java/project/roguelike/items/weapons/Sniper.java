package project.roguelike.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import project.roguelike.core.GameConfig;

public class Sniper extends Weapon {
    public Sniper() {
        super(
                "sniper",
                "Sniper Rifle",
                0.5f,
                1000f,
                5f,
                5,
                2f,
                false);
        this.texture = new Texture("textures/sniper.png");
        setTexture(texture, GameConfig.TILE_SIZE / 2F);
    }
}
