package project.roguelike.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;

public class Shotgun extends Weapon {
    public Shotgun() {
        super(
                "shotgun",
                "Shotgun",
                0.25f,
                500f,
                4f,
                6,
                2f,
                false);
        this.texture = new Texture("textures/shotgun.png");
        setTexture(texture, GameConfig.TILE_SIZE / 2F);
    }

    public Shotgun(Vector2 position) {
        this();
        setPosition(position);
    }
}