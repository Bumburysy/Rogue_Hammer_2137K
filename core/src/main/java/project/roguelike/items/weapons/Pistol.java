package project.roguelike.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import project.roguelike.core.GameConfig;

public class Pistol extends Weapon {
    public Pistol() {
        super(
                "pistol",
                "Pistol",
                0.1f,
                500f,
                1f,
                7,
                1f,
                false);
        this.texture = new Texture("textures/pistol.png");
        setTexture(texture, GameConfig.TILE_SIZE / 2F);
    }
}
